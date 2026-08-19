package com.cibertec.sga.bankexchange.infrastructure.persistence;

import com.cibertec.sga.accountreceivable.domain.model.AccountReceivable;
import com.cibertec.sga.accountreceivable.domain.repository.IAccountReceivableRepository;
import com.cibertec.sga.accountreceivable.infrastructure.persistence.AccountReceivableJpaRepository;
import com.cibertec.sga.bank.domain.model.Bank;
import com.cibertec.sga.bank.domain.repository.IBankRepository;
import com.cibertec.sga.bank.infrastructure.persistence.BankJpaRepository;
import com.cibertec.sga.bankexchange.domain.model.BankExchange;
import com.cibertec.sga.currency.infrastructure.persistence.CurrencyJpaRepository;
import com.cibertec.sga.receipt.domain.model.Receipt;
import com.cibertec.sga.receipt.infrastructure.persistence.ReceiptJpaRepository;
import com.cibertec.sga.receipttype.domain.model.ReceiptType;
import java.time.LocalDate;
import org.springframework.stereotype.Component;

/**
 * Traduce entre {@link BankExchangeEntity}/{@link BankExchangeRow} (persistencia) y
 * {@link BankExchange} (modelo de dominio). Resuelve {@code AccountReceivableId}/{@code
 * BankId}/{@code ReceiptId} desde los {@code Uuid} del modelo de dominio vía los {@code
 * JpaRepository} de esos módulos, y reconstruye la {@code AccountReceivable}/{@code Bank}
 * anidadas al leer vía los repositorios de dominio de esos módulos (dependencia infra-a-infra
 * y a puertos de otros módulos) — evita duplicar el join amplio de {@code AccountReceivable}.
 */
@Component
public class BankExchangeMapper {

    private final AccountReceivableJpaRepository accountReceivableJpaRepository;
    private final BankJpaRepository bankJpaRepository;
    private final ReceiptJpaRepository receiptJpaRepository;
    private final IAccountReceivableRepository accountReceivableRepository;
    private final IBankRepository bankRepository;
    private final CurrencyJpaRepository currencyJpaRepository;

    public BankExchangeMapper(
        AccountReceivableJpaRepository accountReceivableJpaRepository,
        BankJpaRepository bankJpaRepository,
        ReceiptJpaRepository receiptJpaRepository,
        IAccountReceivableRepository accountReceivableRepository,
        IBankRepository bankRepository,
        CurrencyJpaRepository currencyJpaRepository
    ) {
        this.accountReceivableJpaRepository = accountReceivableJpaRepository;
        this.bankJpaRepository = bankJpaRepository;
        this.receiptJpaRepository = receiptJpaRepository;
        this.accountReceivableRepository = accountReceivableRepository;
        this.bankRepository = bankRepository;
        this.currencyJpaRepository = currencyJpaRepository;
    }

    public BankExchangeEntity toNewEntity(AccountReceivable accountReceivable, Bank bank, Receipt receipt, LocalDate depositDate) {
        Long accountReceivableId = accountReceivableJpaRepository.findEntityByUuid(accountReceivable.getUuid()).orElseThrow().getId();
        Long bankId = bankJpaRepository.findEntityByUuid(bank.getUuid()).orElseThrow().getId();
        Long receiptId = receiptJpaRepository.findEntityByUuid(receipt.getUuid()).orElseThrow().getId();
        Long currencyId = currencyJpaRepository.findByUuid(accountReceivable.getCurrency().getUuid()).orElseThrow().getId();
        return BankExchangeEntity.builder()
            .accountReceivableId(accountReceivableId)
            .bankId(bankId)
            .receiptId(receiptId)
            .depositDate(depositDate)
            .amount(accountReceivable.getAmount())
            .currencyId(currencyId)
            .build();
    }

    public BankExchange toDomain(BankExchangeRow row) {
        AccountReceivable accountReceivable = accountReceivableRepository.findByUuid(row.getAccountReceivableUuid()).orElseThrow();
        Bank bank = bankRepository.findByUuid(row.getBankUuid()).orElseThrow();

        ReceiptType receiptType = ReceiptType.builder().uuid(row.getReceiptTypeUuid()).name(row.getReceiptTypeName()).build();
        Receipt receipt = Receipt.builder()
            .uuid(row.getReceiptUuid())
            .receiptType(receiptType)
            .correlativeNumber(row.getReceiptCorrelativeNumber())
            .issueDate(row.getReceiptIssueDate())
            .amount(row.getReceiptAmount())
            .description(row.getReceiptDescription())
            .currency(accountReceivable.getCurrency())
            .build();

        return BankExchange.builder()
            .uuid(row.getUuid())
            .accountReceivable(accountReceivable)
            .bank(bank)
            .receipt(receipt)
            .depositDate(row.getDepositDate())
            .amount(row.getAmount())
            .currency(accountReceivable.getCurrency())
            .build();
    }
}
