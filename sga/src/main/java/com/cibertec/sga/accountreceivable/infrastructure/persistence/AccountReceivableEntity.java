package com.cibertec.sga.accountreceivable.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Fila de la tabla {@code "AccountReceivable"}. {@code Uuid}/{@code CreatedAt}/{@code UpdatedAt}
 * tienen valor por defecto en la base de datos; Hibernate los recupera tras el insert.
 */
@Entity
@Table(name = "AccountReceivable")
@EntityListeners(AuditingEntityListener.class)
public class AccountReceivableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Generated(event = EventType.INSERT)
    @Column(name = "Uuid", insertable = false, updatable = false)
    private UUID uuid;

    @Column(name = "ServiceId", nullable = false)
    private Long serviceId;

    @Column(name = "MemberId")
    private Long memberId;

    @Column(name = "StallId")
    private Long stallId;

    @Column(name = "PeriodStartDate", nullable = false)
    private LocalDate periodStartDate;

    @Column(name = "PeriodEndDate", nullable = false)
    private LocalDate periodEndDate;

    @Column(name = "Amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "AccountReceivableStatusId", nullable = false)
    private Long accountReceivableStatusId;

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

    protected AccountReceivableEntity() {
    }

    private AccountReceivableEntity(Builder builder) {
        this.id = builder.id;
        this.uuid = builder.uuid;
        this.serviceId = builder.serviceId;
        this.memberId = builder.memberId;
        this.stallId = builder.stallId;
        this.periodStartDate = builder.periodStartDate;
        this.periodEndDate = builder.periodEndDate;
        this.amount = builder.amount;
        this.accountReceivableStatusId = builder.accountReceivableStatusId;
    }

    public Long getId() {
        return id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public Long getStallId() {
        return stallId;
    }

    public LocalDate getPeriodStartDate() {
        return periodStartDate;
    }

    public LocalDate getPeriodEndDate() {
        return periodEndDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Long getAccountReceivableStatusId() {
        return accountReceivableStatusId;
    }

    public void setAccountReceivableStatusId(Long accountReceivableStatusId) {
        this.accountReceivableStatusId = accountReceivableStatusId;
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
        private Long serviceId;
        private Long memberId;
        private Long stallId;
        private LocalDate periodStartDate;
        private LocalDate periodEndDate;
        private BigDecimal amount;
        private Long accountReceivableStatusId;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder uuid(UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder serviceId(Long serviceId) {
            this.serviceId = serviceId;
            return this;
        }

        public Builder memberId(Long memberId) {
            this.memberId = memberId;
            return this;
        }

        public Builder stallId(Long stallId) {
            this.stallId = stallId;
            return this;
        }

        public Builder periodStartDate(LocalDate periodStartDate) {
            this.periodStartDate = periodStartDate;
            return this;
        }

        public Builder periodEndDate(LocalDate periodEndDate) {
            this.periodEndDate = periodEndDate;
            return this;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder accountReceivableStatusId(Long accountReceivableStatusId) {
            this.accountReceivableStatusId = accountReceivableStatusId;
            return this;
        }

        public AccountReceivableEntity build() {
            return new AccountReceivableEntity(this);
        }
    }
}
