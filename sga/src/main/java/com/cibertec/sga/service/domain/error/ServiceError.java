package com.cibertec.sga.service.domain.error;

import com.cibertec.sga.common.result.DomainError;
import com.cibertec.sga.common.result.ErrorType;

/**
 * Errores de negocio esperados del módulo {@code Service}.
 */
public sealed interface ServiceError extends DomainError
    permits ServiceError.NotFound, ServiceError.RecurrenceTypeNotFound, ServiceError.ChargeTargetTypeNotFound,
    ServiceError.CurrencyNotFound, ServiceError.InvalidCostConfiguration {

    record NotFound(String uuid) implements ServiceError {
        @Override
        public String code() {
            return "SERVICE_NOT_FOUND";
        }

        @Override
        public String message() {
            return "Servicio no encontrado: " + uuid;
        }

        @Override
        public ErrorType type() {
            return ErrorType.NOT_FOUND;
        }
    }

    record RecurrenceTypeNotFound(String uuid) implements ServiceError {
        @Override
        public String code() {
            return "SERVICE_RECURRENCE_TYPE_NOT_FOUND";
        }

        @Override
        public String message() {
            return "Recurrencia no encontrada: " + uuid;
        }

        @Override
        public ErrorType type() {
            return ErrorType.VALIDATION;
        }
    }

    record ChargeTargetTypeNotFound(String uuid) implements ServiceError {
        @Override
        public String code() {
            return "SERVICE_CHARGE_TARGET_TYPE_NOT_FOUND";
        }

        @Override
        public String message() {
            return "Destino de cobro no encontrado: " + uuid;
        }

        @Override
        public ErrorType type() {
            return ErrorType.VALIDATION;
        }
    }

    record CurrencyNotFound(String uuid) implements ServiceError {
        @Override
        public String code() {
            return "SERVICE_CURRENCY_NOT_FOUND";
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

    record InvalidCostConfiguration() implements ServiceError {
        @Override
        public String code() {
            return "SERVICE_INVALID_COST_CONFIGURATION";
        }

        @Override
        public String message() {
            return "Un servicio por consumo debe indicar costo unitario (sin costo fijo); "
                + "un servicio de costo fijo debe indicar costo (sin costo unitario)";
        }

        @Override
        public ErrorType type() {
            return ErrorType.VALIDATION;
        }
    }
}
