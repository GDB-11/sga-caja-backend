package com.cibertec.sga.currency.domain.error;

import com.cibertec.sga.common.result.DomainError;
import com.cibertec.sga.common.result.ErrorType;

/**
 * Errores de negocio esperados del módulo {@code Currency}.
 */
public sealed interface CurrencyError extends DomainError permits CurrencyError.NotFound {

    record NotFound(String uuid) implements CurrencyError {
        @Override
        public String code() {
            return "CURRENCY_NOT_FOUND";
        }

        @Override
        public String message() {
            return "Moneda no encontrada: " + uuid;
        }

        @Override
        public ErrorType type() {
            return ErrorType.NOT_FOUND;
        }
    }
}
