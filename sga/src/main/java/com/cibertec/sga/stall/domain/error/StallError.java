package com.cibertec.sga.stall.domain.error;

import com.cibertec.sga.common.result.DomainError;
import com.cibertec.sga.common.result.ErrorType;

/**
 * Errores de negocio esperados del módulo {@code Stall}.
 */
public sealed interface StallError extends DomainError
    permits StallError.NotFound, StallError.DuplicateNumber, StallError.BusinessTypeNotFound,
    StallError.MemberNotFound, StallError.InvalidValidityPeriod {

    record NotFound(String uuid) implements StallError {
        @Override
        public String code() {
            return "STALL_NOT_FOUND";
        }

        @Override
        public String message() {
            return "Puesto no encontrado: " + uuid;
        }

        @Override
        public ErrorType type() {
            return ErrorType.NOT_FOUND;
        }
    }

    record DuplicateNumber(String number) implements StallError {
        @Override
        public String code() {
            return "STALL_DUPLICATE_NUMBER";
        }

        @Override
        public String message() {
            return "Ya existe un puesto con número '" + number + "'";
        }

        @Override
        public ErrorType type() {
            return ErrorType.CONFLICT;
        }
    }

    record BusinessTypeNotFound(String uuid) implements StallError {
        @Override
        public String code() {
            return "STALL_BUSINESS_TYPE_NOT_FOUND";
        }

        @Override
        public String message() {
            return "Giro comercial no encontrado: " + uuid;
        }

        @Override
        public ErrorType type() {
            return ErrorType.VALIDATION;
        }
    }

    record MemberNotFound(String uuid) implements StallError {
        @Override
        public String code() {
            return "STALL_MEMBER_NOT_FOUND";
        }

        @Override
        public String message() {
            return "Socio no encontrado: " + uuid;
        }

        @Override
        public ErrorType type() {
            return ErrorType.VALIDATION;
        }
    }

    record InvalidValidityPeriod() implements StallError {
        @Override
        public String code() {
            return "STALL_INVALID_VALIDITY_PERIOD";
        }

        @Override
        public String message() {
            return "La fecha de fin de vigencia debe ser posterior o igual a la fecha de inicio";
        }

        @Override
        public ErrorType type() {
            return ErrorType.VALIDATION;
        }
    }
}
