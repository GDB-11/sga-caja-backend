package com.cibertec.sga.refreshtoken.domain.model;

import java.time.Instant;
import java.util.UUID;

/**
 * Token opaco de refresco de sesión (RF-01-RF-04, RNF-01-RNF-03). Solo se persiste
 * {@code tokenHash} (SHA-256 del valor crudo) — el valor crudo únicamente vive en la cookie
 * {@code httpOnly} del cliente.
 */
public final class RefreshToken {

    private final UUID uuid;
    private final UUID userUuid;
    private final String tokenHash;
    private final Instant expiresAt;
    private final Instant revokedAt;
    private final Instant createdAt;

    private RefreshToken(Builder builder) {
        this.uuid = builder.uuid;
        this.userUuid = builder.userUuid;
        this.tokenHash = builder.tokenHash;
        this.expiresAt = builder.expiresAt;
        this.revokedAt = builder.revokedAt;
        this.createdAt = builder.createdAt;
    }

    public UUID getUuid() {
        return uuid;
    }

    public UUID getUserUuid() {
        return userUuid;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public Instant getRevokedAt() {
        return revokedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public boolean isRevoked() {
        return revokedAt != null;
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public boolean isActive() {
        return !isRevoked() && !isExpired();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private UUID uuid;
        private UUID userUuid;
        private String tokenHash;
        private Instant expiresAt;
        private Instant revokedAt;
        private Instant createdAt;

        public Builder uuid(UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder userUuid(UUID userUuid) {
            this.userUuid = userUuid;
            return this;
        }

        public Builder tokenHash(String tokenHash) {
            this.tokenHash = tokenHash;
            return this;
        }

        public Builder expiresAt(Instant expiresAt) {
            this.expiresAt = expiresAt;
            return this;
        }

        public Builder revokedAt(Instant revokedAt) {
            this.revokedAt = revokedAt;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public RefreshToken build() {
            if (tokenHash == null || tokenHash.isBlank()) {
                throw new IllegalArgumentException("El hash del token es obligatorio");
            }
            if (userUuid == null) {
                throw new IllegalArgumentException("El usuario del token es obligatorio");
            }
            if (expiresAt == null) {
                throw new IllegalArgumentException("La fecha de expiración del token es obligatoria");
            }
            return new RefreshToken(this);
        }
    }
}
