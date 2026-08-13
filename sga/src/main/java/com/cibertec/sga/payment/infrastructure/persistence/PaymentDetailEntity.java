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
import java.util.UUID;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Fila de la tabla {@code "PaymentDetail"}: una cuenta por cobrar incluida en un {@code
 * Payment} (RF-21–RF-23). {@code Uuid} tiene valor por defecto en la base de datos.
 */
@Entity
@Table(name = "PaymentDetail")
@EntityListeners(AuditingEntityListener.class)
public class PaymentDetailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Generated(event = EventType.INSERT)
    @Column(name = "Uuid", insertable = false, updatable = false)
    private UUID uuid;

    @Column(name = "PaymentId", nullable = false)
    private Long paymentId;

    @Column(name = "AccountReceivableId", nullable = false)
    private Long accountReceivableId;

    @Column(name = "Amount", nullable = false)
    private BigDecimal amount;

    @CreatedDate
    @Column(name = "CreatedAt", nullable = false)
    private Instant createdAt;

    protected PaymentDetailEntity() {
    }

    private PaymentDetailEntity(Builder builder) {
        this.id = builder.id;
        this.uuid = builder.uuid;
        this.paymentId = builder.paymentId;
        this.accountReceivableId = builder.accountReceivableId;
        this.amount = builder.amount;
    }

    public Long getId() {
        return id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public Long getAccountReceivableId() {
        return accountReceivableId;
    }

    public BigDecimal getAmount() {
        return amount;
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
        private Long paymentId;
        private Long accountReceivableId;
        private BigDecimal amount;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder uuid(UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder paymentId(Long paymentId) {
            this.paymentId = paymentId;
            return this;
        }

        public Builder accountReceivableId(Long accountReceivableId) {
            this.accountReceivableId = accountReceivableId;
            return this;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public PaymentDetailEntity build() {
            return new PaymentDetailEntity(this);
        }
    }
}
