package com.cibertec.sga.receipt.infrastructure.persistence;

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
 * Fila de la tabla {@code "Receipt"}. {@code Uuid} tiene valor por defecto en la base de datos;
 * {@code CorrelativeNumber} lo asigna un trigger sobre la secuencia del {@code ReceiptType}
 * ({@code fn_receipt_assign_correlative}, RN-04); {@code IssueDate} tiene
 * {@code DEFAULT CURRENT_DATE}. Los tres se marcan {@code insertable = false} y Hibernate los
 * recupera tras el insert, igual que {@code Uuid} en el resto de entidades.
 */
@Entity
@Table(name = "Receipt")
@EntityListeners(AuditingEntityListener.class)
public class ReceiptEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Generated(event = EventType.INSERT)
    @Column(name = "Uuid", insertable = false, updatable = false)
    private UUID uuid;

    @Column(name = "ReceiptTypeId", nullable = false)
    private Long receiptTypeId;

    @Generated(event = EventType.INSERT)
    @Column(name = "CorrelativeNumber", insertable = false, updatable = false)
    private Long correlativeNumber;

    @Generated(event = EventType.INSERT)
    @Column(name = "IssueDate", insertable = false, updatable = false)
    private LocalDate issueDate;

    @Column(name = "Amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "Description")
    private String description;

    @Column(name = "CurrencyId", nullable = false)
    private Long currencyId;

    @CreatedBy
    @Column(name = "UserId", nullable = false, updatable = false)
    private Long userId;

    @CreatedDate
    @Column(name = "CreatedAt", nullable = false)
    private Instant createdAt;

    protected ReceiptEntity() {
    }

    private ReceiptEntity(Builder builder) {
        this.id = builder.id;
        this.uuid = builder.uuid;
        this.receiptTypeId = builder.receiptTypeId;
        this.amount = builder.amount;
        this.description = builder.description;
        this.currencyId = builder.currencyId;
    }

    public Long getId() {
        return id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Long getReceiptTypeId() {
        return receiptTypeId;
    }

    public Long getCorrelativeNumber() {
        return correlativeNumber;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
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
        private Long receiptTypeId;
        private BigDecimal amount;
        private String description;
        private Long currencyId;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder uuid(UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder receiptTypeId(Long receiptTypeId) {
            this.receiptTypeId = receiptTypeId;
            return this;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder currencyId(Long currencyId) {
            this.currencyId = currencyId;
            return this;
        }

        public ReceiptEntity build() {
            return new ReceiptEntity(this);
        }
    }
}
