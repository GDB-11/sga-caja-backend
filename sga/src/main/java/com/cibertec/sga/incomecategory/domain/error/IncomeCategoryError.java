package com.cibertec.sga.incomecategory.domain.error;

import com.cibertec.sga.common.result.DomainError;
import com.cibertec.sga.common.result.ErrorType;

/**
 * Errores de negocio esperados del módulo {@code IncomeCategory}.
 */
public sealed interface IncomeCategoryError extends DomainError permits IncomeCategoryError.NotFound {

    record NotFound(String uuid) implements IncomeCategoryError {
        @Override
        public String code() {
            return "INCOME_CATEGORY_NOT_FOUND";
        }

        @Override
        public String message() {
            return "Categoría de ingreso no encontrada: " + uuid;
        }

        @Override
        public ErrorType type() {
            return ErrorType.NOT_FOUND;
        }
    }
}
