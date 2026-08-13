package com.cibertec.sga.expensereason.web;

import com.cibertec.sga.expensereason.domain.model.ExpenseReason;
import com.cibertec.sga.expensereason.web.dto.ExpenseReasonResponse;
import org.springframework.stereotype.Component;

/**
 * Traduce entre el modelo de dominio {@link ExpenseReason} y los DTOs de {@code web}.
 */
@Component
public class ExpenseReasonDtoMapper {

    public ExpenseReasonResponse toResponse(ExpenseReason expenseReason) {
        return new ExpenseReasonResponse(expenseReason.getUuid(), expenseReason.getName());
    }
}
