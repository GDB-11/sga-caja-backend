package com.cibertec.sga.provider.domain.error;

import com.cibertec.sga.common.result.DomainError;
import com.cibertec.sga.common.result.ErrorType;

/**
 * Errores de negocio esperados del módulo {@code Provider}.
 */
public sealed interface ProviderError extends DomainError permits ProviderError.NotFound {

    record NotFound(String uuid) implements ProviderError {
        @Override
        public String code() {
            return "PROVIDER_NOT_FOUND";
        }

        @Override
        public String message() {
            return "Proveedor no encontrado: " + uuid;
        }

        @Override
        public ErrorType type() {
            return ErrorType.NOT_FOUND;
        }
    }
}
