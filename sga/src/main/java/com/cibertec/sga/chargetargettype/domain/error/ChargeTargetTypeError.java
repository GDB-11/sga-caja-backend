package com.cibertec.sga.chargetargettype.domain.error;

import com.cibertec.sga.common.result.DomainError;
import com.cibertec.sga.common.result.ErrorType;

/**
 * Errores de negocio esperados del módulo {@code ChargeTargetType}.
 */
public sealed interface ChargeTargetTypeError extends DomainError permits ChargeTargetTypeError.NotFound {

    record NotFound(String uuid) implements ChargeTargetTypeError {
        @Override
        public String code() {
            return "CHARGE_TARGET_TYPE_NOT_FOUND";
        }

        @Override
        public String message() {
            return "Destino de cobro no encontrado: " + uuid;
        }

        @Override
        public ErrorType type() {
            return ErrorType.NOT_FOUND;
        }
    }
}
