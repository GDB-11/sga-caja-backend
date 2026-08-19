package com.cibertec.sga.bankexchange.infrastructure.persistence;

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
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Fila de la tabla {@code "BankExchange"}. {@code Uuid} tiene valor por defecto en la base de
 * datos; Hibernate lo recupera tras el insert.
 */
@Entity
@Table(name = "BankExchange")
@EntityListeners(AuditingEntityListener.class)
public class BankExchangeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Generated(event = EventType.INSERT)
    @Column(name = "Uuid", insertable = false, updatable = false)
    private UUID uuid;

    @Column(name = "AccountReceivableId", nullable = false)
    private Long accountReceivableId;

    @Column(name = "BankId", nullable = false)
    private Long bankId;

    @Column(name = "ReceiptId", nullable = false)
    private Long receiptId;

    @Column(name = "DepositDate", nullable = false)
    private LocalDate depositDate;

    @Column(name = "Amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "CurrencyId", nullable = false)
    private Long currencyId;

    @CreatedBy
    @Column(name = "CreatedBy", nullable = false, updatable = false)
    private Long createdBy;

    @CreatedDate
    @Column(name = "CreatedAt", nullable = false)
    private Instant createdAt;

    protected BankExchangeEntity() {
    }

    private BankExchangeEntity(Builder builder) {
        this.id = builder.id;
        this.uuid = builder.uuid;
        this.accountReceivableId = builder.accountReceivableId;
        this.bankId = builder.bankId;
        this.receiptId = builder.receiptId;
        this.depositDate = builder.depositDate;
        this.amount = builder.amount;
        this.currencyId = builder.currencyId;
    }

    public Long getId() {
        return id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Long getAccountReceivableId() {
        return accountReceivableId;
    }

    public Long getBankId() {
        return bankId;
    }

    public Long getReceiptId() {
        return receiptId;
    }

    public LocalDate getDepositDate() {
        return depositDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Long getCurrencyId() {
        return currencyId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Long id;
        private UUID uuid;
        private Long accountReceivableId;
        private Long bankId;
        private Long receiptId;
        private LocalDate depositDate;
        private BigDecimal amount;
        private Long currencyId;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder uuid(UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder accountReceivableId(Long accountReceivableId) {
            this.accountReceivableId = accountReceivableId;
            return this;
        }

        public Builder bankId(Long bankId) {
            this.bankId = bankId;
            return this;
        }

        public Builder receiptId(Long receiptId) {
            this.receiptId = receiptId;
            return this;
        }

        public Builder depositDate(LocalDate depositDate) {
            this.depositDate = depositDate;
            return this;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder currencyId(Long currencyId) {
            this.currencyId = currencyId;
            return this;
        }

        public BankExchangeEntity build() {
            return new BankExchangeEntity(this);
        }
    }
}
