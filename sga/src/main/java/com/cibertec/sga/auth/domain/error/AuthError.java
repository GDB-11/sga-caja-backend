package com.cibertec.sga.auth.domain.error;

import com.cibertec.sga.common.result.DomainError;
import com.cibertec.sga.common.result.ErrorType;

/**
 * Errores de negocio esperados del módulo {@code auth} (RF-01-RF-04, RNF-01-RNF-03).
 */
public sealed interface AuthError extends DomainError
    permits AuthError.InvalidCredentials, AuthError.UserInactive,
    AuthError.InvalidRefreshToken, AuthError.RefreshTokenReused, AuthError.UserNotFound {

    record InvalidCredentials() implements AuthError {
        @Override
        public String code() {
            return "AUTH_INVALID_CREDENTIALS";
        }

        @Override
        public String message() {
            return "Usuario o contraseña incorrectos";
        }

        @Override
        public ErrorType type() {
            return ErrorType.UNAUTHORIZED;
        }
    }

    record UserInactive() implements AuthError {
        @Override
        public String code() {
            return "AUTH_USER_INACTIVE";
        }

        @Override
        public String message() {
            return "El usuario está inactivo";
        }

        @Override
        public ErrorType type() {
            return ErrorType.UNAUTHORIZED;
        }
    }

    record InvalidRefreshToken() implements AuthError {
        @Override
        public String code() {
            return "AUTH_INVALID_REFRESH_TOKEN";
        }

        @Override
        public String message() {
            return "Token de refresco inválido o expirado";
        }

        @Override
        public ErrorType type() {
            return ErrorType.UNAUTHORIZED;
        }
    }

    record RefreshTokenReused() implements AuthError {
        @Override
        public String code() {
            return "AUTH_REFRESH_TOKEN_REUSED";
        }

        @Override
        public String message() {
            return "Token de refresco reutilizado; todas las sesiones fueron cerradas por seguridad";
        }

        @Override
        public ErrorType type() {
            return ErrorType.UNAUTHORIZED;
        }
    }

    record UserNotFound(String uuid) implements AuthError {
        @Override
        public String code() {
            return "AUTH_USER_NOT_FOUND";
        }

        @Override
        public String message() {
            return "Usuario no encontrado: " + uuid;
        }

        @Override
        public ErrorType type() {
            return ErrorType.NOT_FOUND;
        }
    }
}
