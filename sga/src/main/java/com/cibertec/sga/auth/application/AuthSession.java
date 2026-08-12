package com.cibertec.sga.auth.application;

import com.cibertec.sga.user.domain.model.User;

/**
 * Resultado de un login/refresh exitoso. {@code rawRefreshToken} solo lo usa el controller para
 * setear la cookie {@code httpOnly} — nunca se serializa en el cuerpo JSON de la respuesta.
 */
public record AuthSession(
    String accessToken,
    long accessTokenExpiresInSeconds,
    String rawRefreshToken,
    long refreshTokenExpiresInSeconds,
    User user
) {
}
