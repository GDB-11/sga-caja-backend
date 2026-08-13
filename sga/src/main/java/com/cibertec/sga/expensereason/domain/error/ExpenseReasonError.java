package com.cibertec.sga.expensereason.domain.error;

import com.cibertec.sga.common.result.DomainError;
import com.cibertec.sga.common.result.ErrorType;

/**
 * Errores de negocio esperados del módulo {@code ExpenseReason}.
 */
public sealed interface ExpenseReasonError extends DomainError permits ExpenseReasonError.NotFound {

    record NotFound(String uuid) implements ExpenseReasonError {
        @Override
        public String code() {
            return "EXPENSE_REASON_NOT_FOUND";
        }

        @Override
        public String message() {
            return "Motivo de egreso no encontrado: " + uuid;
        }

        @Override
        public ErrorType type() {
            return ErrorType.NOT_FOUND;
        }
    }
}
