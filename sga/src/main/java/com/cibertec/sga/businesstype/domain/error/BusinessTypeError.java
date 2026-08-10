package com.cibertec.sga.businesstype.domain.error;

import com.cibertec.sga.common.result.DomainError;
import com.cibertec.sga.common.result.ErrorType;

/**
 * Errores de negocio esperados del módulo {@code BusinessType}.
 */
public sealed interface BusinessTypeError extends DomainError
    permits BusinessTypeError.NotFound, BusinessTypeError.DuplicateName {

    record NotFound(String uuid) implements BusinessTypeError {
        @Override
        public String code() {
            return "BUSINESS_TYPE_NOT_FOUND";
        }

        @Override
        public String message() {
            return "Giro comercial no encontrado: " + uuid;
        }

        @Override
        public ErrorType type() {
            return ErrorType.NOT_FOUND;
        }
    }

    record DuplicateName(String name) implements BusinessTypeError {
        @Override
        public String code() {
            return "BUSINESS_TYPE_DUPLICATE_NAME";
        }

        @Override
        public String message() {
            return "Ya existe un giro comercial con nombre '" + name + "'";
        }

        @Override
        public ErrorType type() {
            return ErrorType.CONFLICT;
        }
    }
}
