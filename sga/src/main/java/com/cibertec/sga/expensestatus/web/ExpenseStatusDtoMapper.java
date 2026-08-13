package com.cibertec.sga.expensestatus.web;

import com.cibertec.sga.expensestatus.domain.model.ExpenseStatus;
import com.cibertec.sga.expensestatus.web.dto.ExpenseStatusResponse;
import org.springframework.stereotype.Component;

/**
 * Traduce entre el modelo de dominio {@link ExpenseStatus} y los DTOs de {@code web}.
 */
@Component
public class ExpenseStatusDtoMapper {

    public ExpenseStatusResponse toResponse(ExpenseStatus expenseStatus) {
        return new ExpenseStatusResponse(expenseStatus.getUuid(), expenseStatus.getName());
    }
}
