package com.cibertec.sga.accountreceivable.domain.error;

import com.cibertec.sga.common.result.DomainError;
import com.cibertec.sga.common.result.ErrorType;

/**
 * Errores de negocio esperados del módulo {@code AccountReceivable}.
 */
public sealed interface AccountReceivableError extends DomainError
    permits AccountReceivableError.NotFound, AccountReceivableError.ServiceNotFound,
    AccountReceivableError.ServiceInactive, AccountReceivableError.WrongChargeTarget,
    AccountReceivableError.InvalidPeriod, AccountReceivableError.InvalidAmount,
    AccountReceivableError.InvalidStageFilter, AccountReceivableError.NotPending,
    AccountReceivableError.TargetNotFound, AccountReceivableError.InvalidSummaryTarget {

    record NotFound(String uuid) implements AccountReceivableError {
        @Override
        public String code() {
            return "ACCOUNT_RECEIVABLE_NOT_FOUND";
        }

        @Override
        public String message() {
            return "Cuenta por cobrar no encontrada: " + uuid;
        }

        @Override
        public ErrorType type() {
            return ErrorType.NOT_FOUND;
        }
    }

    record ServiceNotFound(String uuid) implements AccountReceivableError {
        @Override
        public String code() {
            return "ACCOUNT_RECEIVABLE_SERVICE_NOT_FOUND";
        }

        @Override
        public String message() {
            return "Servicio no encontrado: " + uuid;
        }

        @Override
        public ErrorType type() {
            return ErrorType.VALIDATION;
        }
    }

    record ServiceInactive(String uuid) implements AccountReceivableError {
        @Override
        public String code() {
            return "ACCOUNT_RECEIVABLE_SERVICE_INACTIVE";
        }

        @Override
        public String message() {
            return "El servicio está inactivo: " + uuid;
        }

        @Override
        public ErrorType type() {
            return ErrorType.VALIDATION;
        }
    }

    record WrongChargeTarget(String serviceName, String expectedTarget) implements AccountReceivableError {
        @Override
        public String code() {
            return "ACCOUNT_RECEIVABLE_WRONG_CHARGE_TARGET";
        }

        @Override
        public String message() {
            return "El servicio '" + serviceName + "' no se carga a " + expectedTarget;
        }

        @Override
        public ErrorType type() {
            return ErrorType.VALIDATION;
        }
    }

    record InvalidPeriod() implements AccountReceivableError {
        @Override
        public String code() {
            return "ACCOUNT_RECEIVABLE_INVALID_PERIOD";
        }

        @Override
        public String message() {
            return "La fecha de fin del período debe ser posterior o igual a la de inicio";
        }

        @Override
        public ErrorType type() {
            return ErrorType.VALIDATION;
        }
    }

    record InvalidAmount(String reason) implements AccountReceivableError {
        @Override
        public String code() {
            return "ACCOUNT_RECEIVABLE_INVALID_AMOUNT";
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

    record InvalidStageFilter() implements AccountReceivableError {
        @Override
        public String code() {
            return "ACCOUNT_RECEIVABLE_INVALID_STAGE_FILTER";
        }

        @Override
        public String message() {
            return "Debe indicar al menos una etapa (1, 2 o 3)";
        }

        @Override
        public ErrorType type() {
            return ErrorType.VALIDATION;
        }
    }

    record NotPending(String uuid) implements AccountReceivableError {
        @Override
        public String code() {
            return "ACCOUNT_RECEIVABLE_NOT_PENDING";
        }

        @Override
        public String message() {
            return "La cuenta por cobrar no está pendiente: " + uuid;
        }

        @Override
        public ErrorType type() {
            return ErrorType.CONFLICT;
        }
    }

    record TargetNotFound(String uuid) implements AccountReceivableError {
        @Override
        public String code() {
            return "ACCOUNT_RECEIVABLE_TARGET_NOT_FOUND";
        }

        @Override
        public String message() {
            return "Socio o puesto no encontrado: " + uuid;
        }

        @Override
        public ErrorType type() {
            return ErrorType.NOT_FOUND;
        }
    }

    record InvalidSummaryTarget() implements AccountReceivableError {
        @Override
        public String code() {
            return "ACCOUNT_RECEIVABLE_INVALID_SUMMARY_TARGET";
        }

        @Override
        public String message() {
            return "Debe indicar exactamente un socio o un puesto para el resumen";
        }

        @Override
        public ErrorType type() {
            return ErrorType.VALIDATION;
        }
    }
}
