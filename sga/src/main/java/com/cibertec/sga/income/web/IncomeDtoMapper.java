package com.cibertec.sga.income.web;

import com.cibertec.sga.income.application.CreateIncomeCommand;
import com.cibertec.sga.income.domain.model.Income;
import com.cibertec.sga.income.web.dto.CreateIncomeRequest;
import com.cibertec.sga.income.web.dto.IncomeResponse;
import org.springframework.stereotype.Component;

/**
 * Traduce entre el modelo de dominio {@link Income} y los DTOs de {@code web}.
 */
@Component
public class IncomeDtoMapper {

    public IncomeResponse toResponse(Income income) {
        return new IncomeResponse(
            income.getUuid(),
            new IncomeResponse.ReceiptRef(
                income.getReceipt().getUuid(), income.getReceipt().getReceiptType().getName(),
                income.getReceipt().getCorrelativeNumber(), income.getReceipt().getIssueDate()
            ),
            income.getDepositorName(),
            new IncomeResponse.IncomeCategoryRef(income.getIncomeCategory().getUuid(), income.getIncomeCategory().getName()),
            new IncomeResponse.CurrencyRef(income.getCurrency().getUuid(), income.getCurrency().getCode(), income.getCurrency().getName()),
            income.getConcept(),
            income.getAmount()
        );
    }

    public CreateIncomeCommand toCommand(CreateIncomeRequest request) {
        return new CreateIncomeCommand(
            request.depositorName(), request.incomeCategoryUuid(), request.currencyUuid(), request.concept(), request.amount()
        );
    }
}
