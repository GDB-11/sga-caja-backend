package com.cibertec.sga.bank.application;

import com.cibertec.sga.bank.domain.error.BankError;
import com.cibertec.sga.bank.domain.model.Bank;
import com.cibertec.sga.bank.domain.repository.IBankRepository;
import com.cibertec.sga.common.result.Result;
import com.cibertec.sga.currency.domain.model.Currency;
import com.cibertec.sga.currency.domain.repository.ICurrencyRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class BankService implements IBankService {

    private final IBankRepository bankRepository;
    private final ICurrencyRepository currencyRepository;

    public BankService(IBankRepository bankRepository, ICurrencyRepository currencyRepository) {
        this.bankRepository = bankRepository;
        this.currencyRepository = currencyRepository;
    }

    @Override
    public Page<Bank> search(String search, Boolean active, Pageable pageable) {
        return bankRepository.search(search, active, pageable);
    }

    @Override
    public Result<Bank, BankError> findByUuid(UUID uuid) {
        return bankRepository.findByUuid(uuid)
            .map(Result::<Bank, BankError>success)
            .orElseGet(() -> Result.failure(new BankError.NotFound(uuid.toString())));
    }

    @Override
    public Result<Bank, BankError> create(BankCommand command) {
        if (bankRepository.existsByAccountNumber(command.accountNumber())) {
            return Result.failure(new BankError.DuplicateAccountNumber(command.accountNumber()));
        }

        Optional<Currency> currency = currencyRepository.findByUuid(command.currencyUuid());
        if (currency.isEmpty()) {
            return Result.failure(new BankError.CurrencyNotFound(command.currencyUuid().toString()));
        }

        Bank bank = Bank.builder()
            .name(command.name())
            .accountNumber(command.accountNumber())
            .cci(command.cci())
            .currency(currency.get())
            .build();
        return Result.success(bankRepository.insert(bank));
    }

    @Override
    public Result<Bank, BankError> update(UUID uuid, BankCommand command) {
        if (bankRepository.findByUuid(uuid).isEmpty()) {
            return Result.failure(new BankError.NotFound(uuid.toString()));
        }

        if (bankRepository.existsByAccountNumberAndUuidNot(command.accountNumber(), uuid)) {
            return Result.failure(new BankError.DuplicateAccountNumber(command.accountNumber()));
        }

        Optional<Currency> currency = currencyRepository.findByUuid(command.currencyUuid());
        if (currency.isEmpty()) {
            return Result.failure(new BankError.CurrencyNotFound(command.currencyUuid().toString()));
        }

        Bank bank = Bank.builder()
            .uuid(uuid)
            .name(command.name())
            .accountNumber(command.accountNumber())
            .cci(command.cci())
            .currency(currency.get())
            .build();
        return Result.success(bankRepository.update(uuid, bank));
    }

    @Override
    public Result<Bank, BankError> deactivate(UUID uuid) {
        if (bankRepository.findByUuid(uuid).isEmpty()) {
            return Result.failure(new BankError.NotFound(uuid.toString()));
        }

        return Result.success(bankRepository.deactivate(uuid));
    }
}
