package com.cibertec.sga.bank.web;

import com.cibertec.sga.bank.application.BankCommand;
import com.cibertec.sga.bank.domain.model.Bank;
import com.cibertec.sga.bank.web.dto.BankRequest;
import com.cibertec.sga.bank.web.dto.BankResponse;
import org.springframework.stereotype.Component;

/**
 * Traduce entre el modelo de dominio {@link Bank} y los DTOs de {@code web}.
 */
@Component
public class BankDtoMapper {

    public BankResponse toResponse(Bank bank) {
        return new BankResponse(
            bank.getUuid(),
            bank.getName(),
            bank.getAccountNumber(),
            bank.getCci(),
            new BankResponse.CurrencyRef(bank.getCurrency().getUuid(), bank.getCurrency().getCode(), bank.getCurrency().getName()),
            bank.isActive()
        );
    }

    public BankCommand toCommand(BankRequest request) {
        return new BankCommand(request.name(), request.accountNumber(), request.cci(), request.currencyUuid());
    }
}
