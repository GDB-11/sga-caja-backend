package com.cibertec.sga.auth.application;

import com.cibertec.sga.auth.domain.error.AuthError;
import com.cibertec.sga.common.result.Result;
import com.cibertec.sga.user.domain.model.User;
import java.util.UUID;

/**
 * Casos de uso de sesión (RF-01-RF-04): login, refresco con rotación, logout y perfil del
 * usuario autenticado. Es la única interfaz que se inyecta en {@code AuthController}.
 */
public interface IAuthService {

    Result<AuthSession, AuthError> login(String username, String rawPassword);

    Result<AuthSession, AuthError> refresh(String rawRefreshToken);

    Result<Void, AuthError> logout(String rawRefreshToken);

    Result<User, AuthError> me(UUID userUuid);
}
