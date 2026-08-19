package com.cibertec.sga.accountreceivable.web;

import com.cibertec.sga.accountreceivable.application.GenerateByMemberCommand;
import com.cibertec.sga.accountreceivable.application.GenerateByStallCommand;
import com.cibertec.sga.accountreceivable.domain.model.AccountReceivable;
import com.cibertec.sga.accountreceivable.domain.model.AccountReceivableMovement;
import com.cibertec.sga.accountreceivable.web.dto.AccountReceivableMovementResponse;
import com.cibertec.sga.accountreceivable.web.dto.AccountReceivableResponse;
import com.cibertec.sga.accountreceivable.web.dto.GenerateByMemberRequest;
import com.cibertec.sga.accountreceivable.web.dto.GenerateByStallRequest;
import org.springframework.stereotype.Component;

/**
 * Traduce entre el modelo de dominio {@link AccountReceivable} y los DTOs de {@code web}.
 */
@Component
public class AccountReceivableDtoMapper {

    public AccountReceivableResponse toResponse(AccountReceivable accountReceivable) {
        return new AccountReceivableResponse(
            accountReceivable.getUuid(),
            new AccountReceivableResponse.ServiceRef(
                accountReceivable.getService().getUuid(),
                accountReceivable.getService().getName(),
                accountReceivable.getService().isConsumptionBased()
            ),
            accountReceivable.getMember() == null
                ? null
                : new AccountReceivableResponse.MemberRef(
                    accountReceivable.getMember().uuid(), accountReceivable.getMember().fullName()
                ),
            accountReceivable.getStall() == null
                ? null
                : new AccountReceivableResponse.StallRef(
                    accountReceivable.getStall().uuid(), accountReceivable.getStall().number()
                ),
            accountReceivable.getPeriodStartDate(),
            accountReceivable.getPeriodEndDate(),
            accountReceivable.getAmount(),
            new AccountReceivableResponse.StatusRef(
                accountReceivable.getStatus().getUuid(), accountReceivable.getStatus().getName()
            ),
            new AccountReceivableResponse.CurrencyRef(
                accountReceivable.getCurrency().getUuid(), accountReceivable.getCurrency().getCode(),
                accountReceivable.getCurrency().getName()
            )
        );
    }

    public GenerateByStallCommand toCommand(GenerateByStallRequest request) {
        return new GenerateByStallCommand(
            request.serviceUuid(), request.periodStartDate(), request.periodEndDate(), request.amount()
        );
    }

    public GenerateByMemberCommand toCommand(GenerateByMemberRequest request) {
        return new GenerateByMemberCommand(
            request.serviceUuid(), request.periodStartDate(), request.periodEndDate(), request.amount(),
            request.stageCodes(), request.uniqueMembers()
        );
    }

    public AccountReceivableMovementResponse toResponse(AccountReceivableMovement movement) {
        return new AccountReceivableMovementResponse(
            toResponse(movement.accountReceivable()), movement.settlementMethod(), movement.settledDate(),
            movement.receiptCorrelative()
        );
    }
}
