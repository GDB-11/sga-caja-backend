package com.cibertec.sga.bank.infrastructure.persistence;

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
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Fila de la tabla {@code "Bank"}. {@code Uuid}/{@code CreatedAt}/{@code UpdatedAt} tienen
 * valor por defecto en la base de datos; Hibernate los recupera tras el insert.
 */
@Entity
@Table(name = "Bank")
@EntityListeners(AuditingEntityListener.class)
public class BankEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Generated(event = EventType.INSERT)
    @Column(name = "Uuid", insertable = false, updatable = false)
    private UUID uuid;

    @Column(name = "Name", nullable = false)
    private String name;

    @Column(name = "AccountNumber", nullable = false)
    private String accountNumber;

    @Column(name = "Cci", nullable = false)
    private String cci;

    @Column(name = "CurrencyId", nullable = false)
    private Long currencyId;

    @Column(name = "IsActive", nullable = false)
    private boolean active;

    @CreatedBy
    @Column(name = "CreatedBy", nullable = false, updatable = false)
    private Long createdBy;

    @LastModifiedBy
    @Column(name = "UpdatedBy", nullable = false)
    private Long updatedBy;

    @CreatedDate
    @Column(name = "CreatedAt", nullable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "UpdatedAt", nullable = false)
    private Instant updatedAt;

    protected BankEntity() {
    }

    private BankEntity(Builder builder) {
        this.id = builder.id;
        this.uuid = builder.uuid;
        this.name = builder.name;
        this.accountNumber = builder.accountNumber;
        this.cci = builder.cci;
        this.currencyId = builder.currencyId;
        this.active = builder.active;
    }

    public Long getId() {
        return id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getCci() {
        return cci;
    }

    public void setCci(String cci) {
        this.cci = cci;
    }

    public Long getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Long currencyId) {
        this.currencyId = currencyId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Long id;
        private UUID uuid;
        private String name;
        private String accountNumber;
        private String cci;
        private Long currencyId;
        private boolean active = true;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder uuid(UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder accountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
            return this;
        }

        public Builder cci(String cci) {
            this.cci = cci;
            return this;
        }

        public Builder currencyId(Long currencyId) {
            this.currencyId = currencyId;
            return this;
        }

        public Builder active(boolean active) {
            this.active = active;
            return this;
        }

        public BankEntity build() {
            return new BankEntity(this);
        }
    }
}
