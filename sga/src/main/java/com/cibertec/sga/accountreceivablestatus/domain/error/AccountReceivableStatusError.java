package com.cibertec.sga.accountreceivablestatus.domain.error;

import com.cibertec.sga.common.result.DomainError;
import com.cibertec.sga.common.result.ErrorType;

/**
 * Errores de negocio esperados del módulo {@code AccountReceivableStatus}.
 */
public sealed interface AccountReceivableStatusError extends DomainError
    permits AccountReceivableStatusError.NotFound {

    record NotFound(String uuid) implements AccountReceivableStatusError {
        @Override
        public String code() {
            return "ACCOUNT_RECEIVABLE_STATUS_NOT_FOUND";
        }

        @Override
        public String message() {
            return "Estado de cuenta por cobrar no encontrado: " + uuid;
        }

        @Override
        public ErrorType type() {
            return ErrorType.NOT_FOUND;
        }
    }
}
