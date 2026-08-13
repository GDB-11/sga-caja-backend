package com.cibertec.sga.income.infrastructure.persistence;

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
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Fila de la tabla {@code "Income"}. {@code Uuid} tiene valor por defecto en la base de datos;
 * Hibernate lo recupera tras el insert.
 */
@Entity
@Table(name = "Income")
@EntityListeners(AuditingEntityListener.class)
public class IncomeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Generated(event = EventType.INSERT)
    @Column(name = "Uuid", insertable = false, updatable = false)
    private UUID uuid;

    @Column(name = "ReceiptId", nullable = false)
    private Long receiptId;

    @Column(name = "DepositorName", nullable = false)
    private String depositorName;

    @Column(name = "IncomeCategoryId", nullable = false)
    private Long incomeCategoryId;

    @Column(name = "Concept", nullable = false)
    private String concept;

    @Column(name = "Amount", nullable = false)
    private BigDecimal amount;

    @CreatedBy
    @Column(name = "CreatedBy", nullable = false, updatable = false)
    private Long createdBy;

    @CreatedDate
    @Column(name = "CreatedAt", nullable = false)
    private Instant createdAt;

    protected IncomeEntity() {
    }

    private IncomeEntity(Builder builder) {
        this.id = builder.id;
        this.uuid = builder.uuid;
        this.receiptId = builder.receiptId;
        this.depositorName = builder.depositorName;
        this.incomeCategoryId = builder.incomeCategoryId;
        this.concept = builder.concept;
        this.amount = builder.amount;
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

    public String getDepositorName() {
        return depositorName;
    }

    public Long getIncomeCategoryId() {
        return incomeCategoryId;
    }

    public String getConcept() {
        return concept;
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
        private Long receiptId;
        private String depositorName;
        private Long incomeCategoryId;
        private String concept;
        private BigDecimal amount;

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

        public Builder depositorName(String depositorName) {
            this.depositorName = depositorName;
            return this;
        }

        public Builder incomeCategoryId(Long incomeCategoryId) {
            this.incomeCategoryId = incomeCategoryId;
            return this;
        }

        public Builder concept(String concept) {
            this.concept = concept;
            return this;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public IncomeEntity build() {
            return new IncomeEntity(this);
        }
    }
}
