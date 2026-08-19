package com.cibertec.sga.payment.infrastructure.persistence;

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
 * Fila de la tabla {@code "Payment"}. {@code Uuid}/{@code PaymentDate} tienen valor por
 * defecto en la base de datos; Hibernate los recupera tras el insert.
 */
@Entity
@Table(name = "Payment")
@EntityListeners(AuditingEntityListener.class)
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Generated(event = EventType.INSERT)
    @Column(name = "Uuid", insertable = false, updatable = false)
    private UUID uuid;

    @Column(name = "ReceiptId", nullable = false)
    private Long receiptId;

    @Generated(event = EventType.INSERT)
    @Column(name = "PaymentDate", insertable = false, updatable = false)
    private LocalDate paymentDate;

    @Column(name = "TotalAmount", nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "CurrencyId", nullable = false)
    private Long currencyId;

    @CreatedBy
    @Column(name = "CreatedBy", nullable = false, updatable = false)
    private Long createdBy;

    @CreatedDate
    @Column(name = "CreatedAt", nullable = false)
    private Instant createdAt;

    protected PaymentEntity() {
    }

    private PaymentEntity(Builder builder) {
        this.id = builder.id;
        this.uuid = builder.uuid;
        this.receiptId = builder.receiptId;
        this.totalAmount = builder.totalAmount;
        this.currencyId = builder.currencyId;
    }

    public Long getId() {
        return id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Long getReceiptId() {
        return receiptId;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
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
        private Long receiptId;
        private BigDecimal totalAmount;
        private Long currencyId;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder uuid(UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder receiptId(Long receiptId) {
            this.receiptId = receiptId;
            return this;
        }

        public Builder totalAmount(BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
            return this;
        }

        public Builder currencyId(Long currencyId) {
            this.currencyId = currencyId;
            return this;
        }

        public PaymentEntity build() {
            return new PaymentEntity(this);
        }
    }
}
