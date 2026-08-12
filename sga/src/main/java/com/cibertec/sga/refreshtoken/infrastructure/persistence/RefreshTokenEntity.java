package com.cibertec.sga.refreshtoken.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Fila de la tabla {@code "RefreshToken"}. {@code Uuid}/{@code CreatedAt} tienen valor por
 * defecto en la base de datos; Hibernate los recupera tras el insert.
 */
@Entity
@Table(name = "RefreshToken")
@EntityListeners(AuditingEntityListener.class)
public class RefreshTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Generated(event = EventType.INSERT)
    @Column(name = "Uuid", insertable = false, updatable = false)
    private UUID uuid;

    @Column(name = "UserId", nullable = false)
    private Long userId;

    @Column(name = "TokenHash", nullable = false)
    private String tokenHash;

    @Column(name = "ExpiresAt", nullable = false)
    private Instant expiresAt;

    @Column(name = "RevokedAt")
    private Instant revokedAt;

    @CreatedDate
    @Column(name = "CreatedAt", nullable = false)
    private Instant createdAt;

    protected RefreshTokenEntity() {
    }

    private RefreshTokenEntity(Builder builder) {
        this.userId = builder.userId;
        this.tokenHash = builder.tokenHash;
        this.expiresAt = builder.expiresAt;
        this.revokedAt = builder.revokedAt;
    }

    public Long getId() {
        return id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Long getUserId() {
        return userId;
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

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Long userId;
        private String tokenHash;
        private Instant expiresAt;
        private Instant revokedAt;

        public Builder userId(Long userId) {
            this.userId = userId;
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

        public RefreshTokenEntity build() {
            return new RefreshTokenEntity(this);
        }
    }
}
