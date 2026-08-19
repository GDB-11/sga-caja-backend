package com.cibertec.sga.bankexchange.application;

import com.cibertec.sga.accountreceivable.domain.model.AccountReceivable;
import com.cibertec.sga.accountreceivable.domain.repository.IAccountReceivableRepository;
import com.cibertec.sga.accountreceivablestatus.domain.model.AccountReceivableStatus;
import com.cibertec.sga.accountreceivablestatus.domain.repository.IAccountReceivableStatusRepository;
import com.cibertec.sga.bank.domain.model.Bank;
import com.cibertec.sga.bank.domain.repository.IBankRepository;
import com.cibertec.sga.bankexchange.domain.error.BankExchangeError;
import com.cibertec.sga.bankexchange.domain.model.BankExchange;
import com.cibertec.sga.bankexchange.domain.repository.IBankExchangeRepository;
import com.cibertec.sga.common.result.Result;
import com.cibertec.sga.receipt.domain.model.Receipt;
import com.cibertec.sga.receipt.domain.repository.IReceiptRepository;
import com.cibertec.sga.receipttype.domain.model.ReceiptType;
import com.cibertec.sga.receipttype.domain.repository.IReceiptTypeRepository;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BankExchangeService implements IBankExchangeService {

    private static final String STATUS_PENDING = "Pending";
    private static final String STATUS_PAID = "Paid";
    private static final String RECEIPT_TYPE_BANK_TRANSACTION = "BankTransaction";

    private final IBankExchangeRepository bankExchangeRepository;
    private final IAccountReceivableRepository accountReceivableRepository;
    private final IAccountReceivableStatusRepository accountReceivableStatusRepository;
    private final IBankRepository bankRepository;
    private final IReceiptRepository receiptRepository;
    private final IReceiptTypeRepository receiptTypeRepository;

    public BankExchangeService(
        IBankExchangeRepository bankExchangeRepository,
        IAccountReceivableRepository accountReceivableRepository,
        IAccountReceivableStatusRepository accountReceivableStatusRepository,
        IBankRepository bankRepository,
        IReceiptRepository receiptRepository,
        IReceiptTypeRepository receiptTypeRepository
    ) {
        this.bankExchangeRepository = bankExchangeRepository;
        this.accountReceivableRepository = accountReceivableRepository;
        this.accountReceivableStatusRepository = accountReceivableStatusRepository;
        this.bankRepository = bankRepository;
        this.receiptRepository = receiptRepository;
        this.receiptTypeRepository = receiptTypeRepository;
    }

    @Override
    @Transactional
    public Result<BankExchange, BankExchangeError> create(CreateBankExchangeCommand command) {
        var accountReceivableOpt = accountReceivableRepository.findByUuidForUpdate(command.accountReceivableUuid());
        if (accountReceivableOpt.isEmpty()) {
            return Result.failure(new BankExchangeError.AccountReceivableNotFound(command.accountReceivableUuid().toString()));
        }
        AccountReceivable accountReceivable = accountReceivableOpt.get();

        if (accountReceivable.getMember() == null) {
            return Result.failure(new BankExchangeError.AccountReceivableNotMemberTarget(command.accountReceivableUuid().toString()));
        }
        if (!accountReceivable.getStatus().getName().equals(STATUS_PENDING)) {
            return Result.failure(new BankExchangeError.AccountReceivableNotPending(command.accountReceivableUuid().toString()));
        }

        var bankOpt = bankRepository.findByUuid(command.bankUuid());
        if (bankOpt.isEmpty()) {
            return Result.failure(new BankExchangeError.BankNotFound(command.bankUuid().toString()));
        }
        Bank bank = bankOpt.get();
        if (!bank.isActive()) {
            return Result.failure(new BankExchangeError.BankInactive(command.bankUuid().toString()));
        }
        if (!bank.getCurrency().getUuid().equals(accountReceivable.getCurrency().getUuid())) {
            return Result.failure(new BankExchangeError.CurrencyMismatch(
                accountReceivable.getCurrency().getCode(), bank.getCurrency().getCode()
            ));
        }

        ReceiptType bankTransactionType = receiptTypeRepository.findByName(RECEIPT_TYPE_BANK_TRANSACTION).orElseThrow();
        Receipt receipt = receiptRepository.insert(
            Receipt.builder().receiptType(bankTransactionType).amount(accountReceivable.getAmount())
                .currency(accountReceivable.getCurrency()).build()
        );

        BankExchange bankExchange = bankExchangeRepository.create(accountReceivable, bank, receipt, command.depositDate());

        AccountReceivableStatus paidStatus = accountReceivableStatusRepository.findByName(STATUS_PAID).orElseThrow();
        accountReceivableRepository.updateStatus(accountReceivable.getUuid(), paidStatus);

        return Result.success(bankExchange);
    }

    @Override
    public Result<BankExchange, BankExchangeError> findByUuid(UUID uuid) {
        return bankExchangeRepository.findByUuid(uuid)
            .map(Result::<BankExchange, BankExchangeError>success)
            .orElseGet(() -> Result.failure(new BankExchangeError.NotFound(uuid.toString())));
    }

    @Override
    public Page<BankExchange> search(UUID bankUuid, LocalDate date, Pageable pageable) {
        return bankExchangeRepository.search(bankUuid, date, pageable);
    }
}
