package com.cibertec.sga.common.security;

import java.util.Optional;
import java.util.UUID;

/**
 * Emisión y validación del access token JWT. Lleva únicamente el {@code Uuid} del usuario como
 * subject — nunca el {@code Id} interno ni rol/estado, que {@code JwtAuthenticationFilter}
 * siempre re-resuelve contra la base de datos en cada request (RNF-02).
 */
public interface IJwtService {

    String generateAccessToken(UUID userUuid);

    Optional<UUID> validateAndGetSubject(String token);

    long getAccessTokenExpirySeconds();
}
