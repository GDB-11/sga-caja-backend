package com.cibertec.sga.consumptionreading.domain.error;

import com.cibertec.sga.common.result.DomainError;
import com.cibertec.sga.common.result.ErrorType;

/**
 * Errores de negocio esperados del módulo {@code ConsumptionReading}.
 */
public sealed interface ConsumptionReadingError extends DomainError
    permits ConsumptionReadingError.NotFound, ConsumptionReadingError.AccountReceivableNotFound,
    ConsumptionReadingError.ServiceNotConsumptionBased, ConsumptionReadingError.DuplicateReading {

    record NotFound(String uuid) implements ConsumptionReadingError {
        @Override
        public String code() {
            return "CONSUMPTION_READING_NOT_FOUND";
        }

        @Override
        public String message() {
            return "Lectura de consumo no encontrada: " + uuid;
        }

        @Override
        public ErrorType type() {
            return ErrorType.NOT_FOUND;
        }
    }

    record AccountReceivableNotFound(String uuid) implements ConsumptionReadingError {
        @Override
        public String code() {
            return "CONSUMPTION_READING_ACCOUNT_RECEIVABLE_NOT_FOUND";
        }

        @Override
        public String message() {
            return "Cuenta por cobrar no encontrada: " + uuid;
        }

        @Override
        public ErrorType type() {
            return ErrorType.VALIDATION;
        }
    }

    record ServiceNotConsumptionBased(String accountReceivableUuid) implements ConsumptionReadingError {
        @Override
        public String code() {
            return "CONSUMPTION_READING_SERVICE_NOT_CONSUMPTION_BASED";
        }

        @Override
        public String message() {
            return "El servicio de la cuenta por cobrar " + accountReceivableUuid + " no es por consumo";
        }

        @Override
        public ErrorType type() {
            return ErrorType.VALIDATION;
        }
    }

    record DuplicateReading(String accountReceivableUuid) implements ConsumptionReadingError {
        @Override
        public String code() {
            return "CONSUMPTION_READING_DUPLICATE";
        }

        @Override
        public String message() {
            return "Ya existe una lectura registrada para la cuenta por cobrar " + accountReceivableUuid;
        }

        @Override
        public ErrorType type() {
            return ErrorType.CONFLICT;
        }
    }
}
