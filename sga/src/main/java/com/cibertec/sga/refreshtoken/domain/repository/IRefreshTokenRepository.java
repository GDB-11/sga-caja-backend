package com.cibertec.sga.refreshtoken.domain.repository;

import com.cibertec.sga.refreshtoken.domain.model.RefreshToken;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de persistencia para {@link RefreshToken}, consumido por el módulo {@code auth}.
 */
public interface IRefreshTokenRepository {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    RefreshToken insert(RefreshToken refreshToken);

    void revokeByTokenHash(String tokenHash);

    /**
     * Revoca todos los tokens activos del usuario — respuesta ante reuso detectado de un token
     * ya revocado (posible robo/replay), fuerza a re-autenticar en todas las sesiones.
     */
    void revokeAllActiveByUserUuid(UUID userUuid);
}
