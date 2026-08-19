package com.cibertec.sga.expense.domain.error;

import com.cibertec.sga.common.result.DomainError;
import com.cibertec.sga.common.result.ErrorType;
import java.util.List;

/**
 * Errores de negocio esperados del módulo {@code Expense}.
 */
public sealed interface ExpenseError extends DomainError
    permits ExpenseError.NotFound, ExpenseError.ProviderNotFound, ExpenseError.ProviderInactive,
    ExpenseError.ExpenseReasonNotFound, ExpenseError.CurrencyNotFound, ExpenseError.NotPending,
    ExpenseError.InvalidBulkFile, ExpenseError.BulkValidationFailed {

    record NotFound(String uuid) implements ExpenseError {
        @Override
        public String code() {
            return "EXPENSE_NOT_FOUND";
        }

        @Override
        public String message() {
            return "Egreso no encontrado: " + uuid;
        }

        @Override
        public ErrorType type() {
            return ErrorType.NOT_FOUND;
        }
    }

    record ProviderNotFound(String uuid) implements ExpenseError {
        @Override
        public String code() {
            return "EXPENSE_PROVIDER_NOT_FOUND";
        }

        @Override
        public String message() {
            return "Proveedor no encontrado: " + uuid;
        }

        @Override
        public ErrorType type() {
            return ErrorType.VALIDATION;
        }
    }

    record ProviderInactive(String uuid) implements ExpenseError {
        @Override
        public String code() {
            return "EXPENSE_PROVIDER_INACTIVE";
        }

        @Override
        public String message() {
            return "El proveedor está inactivo: " + uuid;
        }

        @Override
        public ErrorType type() {
            return ErrorType.VALIDATION;
        }
    }

    record ExpenseReasonNotFound(String uuid) implements ExpenseError {
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
            return ErrorType.VALIDATION;
        }
    }

    record CurrencyNotFound(String uuid) implements ExpenseError {
        @Override
        public String code() {
            return "EXPENSE_CURRENCY_NOT_FOUND";
        }

        @Override
        public String message() {
            return "Moneda no encontrada: " + uuid;
        }

        @Override
        public ErrorType type() {
            return ErrorType.VALIDATION;
        }
    }

    record NotPending(String uuid) implements ExpenseError {
        @Override
        public String code() {
            return "EXPENSE_NOT_PENDING";
        }

        @Override
        public String message() {
            return "El egreso no está pendiente: " + uuid;
        }

        @Override
        public ErrorType type() {
            return ErrorType.CONFLICT;
        }
    }

    record InvalidBulkFile(String reason) implements ExpenseError {
        @Override
        public String code() {
            return "EXPENSE_INVALID_BULK_FILE";
        }

        @Override
        public String message() {
            return reason;
        }

        @Override
        public ErrorType type() {
            return ErrorType.VALIDATION;
        }
    }

    record BulkValidationFailed(List<String> rowErrors) implements ExpenseError {
        @Override
        public String code() {
            return "EXPENSE_BULK_VALIDATION_FAILED";
        }

        @Override
        public String message() {
            return String.join("; ", rowErrors);
        }

        @Override
        public ErrorType type() {
            return ErrorType.VALIDATION;
        }
    }
}
