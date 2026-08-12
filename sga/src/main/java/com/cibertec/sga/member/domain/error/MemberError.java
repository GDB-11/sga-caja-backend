package com.cibertec.sga.member.domain.error;

import com.cibertec.sga.common.result.DomainError;
import com.cibertec.sga.common.result.ErrorType;

/**
 * Errores de negocio esperados del módulo {@code Member}.
 */
public sealed interface MemberError extends DomainError
    permits MemberError.NotFound, MemberError.DuplicateCode, MemberError.StageNotFound {

    record NotFound(String uuid) implements MemberError {
        @Override
        public String code() {
            return "MEMBER_NOT_FOUND";
        }

        @Override
        public String message() {
            return "Socio no encontrado: " + uuid;
        }

        @Override
        public ErrorType type() {
            return ErrorType.NOT_FOUND;
        }
    }

    record DuplicateCode(String memberCode) implements MemberError {
        @Override
        public String code() {
            return "MEMBER_DUPLICATE_CODE";
        }

        @Override
        public String message() {
            return "Ya existe un socio con código '" + memberCode + "'";
        }

        @Override
        public ErrorType type() {
            return ErrorType.CONFLICT;
        }
    }

    record StageNotFound(String stageUuid) implements MemberError {
        @Override
        public String code() {
            return "MEMBER_STAGE_NOT_FOUND";
        }

        @Override
        public String message() {
            return "Etapa no encontrada: " + stageUuid;
        }

        @Override
        public ErrorType type() {
            return ErrorType.VALIDATION;
        }
    }
}
