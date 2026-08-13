package com.cibertec.sga.expensestatus.domain.error;

import com.cibertec.sga.common.result.DomainError;
import com.cibertec.sga.common.result.ErrorType;

/**
 * Errores de negocio esperados del módulo {@code ExpenseStatus}.
 */
public sealed interface ExpenseStatusError extends DomainError permits ExpenseStatusError.NotFound {

    record NotFound(String uuid) implements ExpenseStatusError {
        @Override
        public String code() {
            return "EXPENSE_STATUS_NOT_FOUND";
        }

        @Override
        public String message() {
            return "Estado de egreso no encontrado: " + uuid;
        }

        @Override
        public ErrorType type() {
            return ErrorType.NOT_FOUND;
        }
    }
}
