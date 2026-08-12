package com.cibertec.sga.stage.domain.error;

import com.cibertec.sga.common.result.DomainError;
import com.cibertec.sga.common.result.ErrorType;

/**
 * Errores de negocio esperados del módulo {@code Stage}.
 */
public sealed interface StageError extends DomainError permits StageError.NotFound {

    record NotFound(String uuid) implements StageError {
        @Override
        public String code() {
            return "STAGE_NOT_FOUND";
        }

        @Override
        public String message() {
            return "Etapa no encontrada: " + uuid;
        }

        @Override
        public ErrorType type() {
            return ErrorType.NOT_FOUND;
        }
    }
}
