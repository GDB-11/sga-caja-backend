package com.cibertec.sga.common.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtService implements IJwtService {

    private final SecretKey signingKey;
    private final Duration accessTokenTtl;

    public JwtService(
        @Value("${sga.security.jwt.secret}") String secret,
        @Value("${sga.security.jwt.access-token-ttl-minutes}") long accessTokenTtlMinutes
    ) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenTtl = Duration.ofMinutes(accessTokenTtlMinutes);
    }

    @Override
    public String generateAccessToken(UUID userUuid) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + accessTokenTtl.toMillis());

        return Jwts.builder()
            .subject(userUuid.toString())
            .issuedAt(now)
            .expiration(expiration)
            .signWith(signingKey, Jwts.SIG.HS256)
            .compact();
    }

    @Override
    public Optional<UUID> validateAndGetSubject(String token) {
        try {
            String subject = Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
            return Optional.of(UUID.fromString(subject));
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    @Override
    public long getAccessTokenExpirySeconds() {
        return accessTokenTtl.toSeconds();
    }
}
