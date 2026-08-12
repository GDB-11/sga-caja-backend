package com.cibertec.sga.recurrencetype.domain.error;

import com.cibertec.sga.common.result.DomainError;
import com.cibertec.sga.common.result.ErrorType;

/**
 * Errores de negocio esperados del módulo {@code RecurrenceType}.
 */
public sealed interface RecurrenceTypeError extends DomainError permits RecurrenceTypeError.NotFound {

    record NotFound(String uuid) implements RecurrenceTypeError {
        @Override
        public String code() {
            return "RECURRENCE_TYPE_NOT_FOUND";
        }

        @Override
        public String message() {
            return "Tipo de recurrencia no encontrado: " + uuid;
        }

        @Override
        public ErrorType type() {
            return ErrorType.NOT_FOUND;
        }
    }
}
