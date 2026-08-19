package com.cibertec.sga.expense.infrastructure.persistence;

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
 * Fila de la tabla {@code "Expense"}. {@code Uuid}/{@code CreatedAt}/{@code UpdatedAt} tienen
 * valor por defecto en la base de datos; Hibernate los recupera tras el insert.
 * {@code ExpenseBulkUploadId}/{@code ReceiptId} quedan {@code null} hasta que el egreso viene
 * de una carga masiva o se procesa, respectivamente.
 */
@Entity
@Table(name = "Expense")
@EntityListeners(AuditingEntityListener.class)
public class ExpenseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Generated(event = EventType.INSERT)
    @Column(name = "Uuid", insertable = false, updatable = false)
    private UUID uuid;

    @Column(name = "DocumentNumber", nullable = false)
    private String documentNumber;

    @Column(name = "ProviderId", nullable = false)
    private Long providerId;

    @Column(name = "ExpenseDate", nullable = false)
    private LocalDate expenseDate;

    @Column(name = "Amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "AssociatedDocument")
    private String associatedDocument;

    @Column(name = "ExpenseReasonId", nullable = false)
    private Long expenseReasonId;

    @Column(name = "ExpenseStatusId", nullable = false)
    private Long expenseStatusId;

    @Column(name = "ExpenseBulkUploadId")
    private Long expenseBulkUploadId;

    @Column(name = "ReceiptId")
    private Long receiptId;

    @Column(name = "CurrencyId", nullable = false)
    private Long currencyId;

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

    protected ExpenseEntity() {
    }

    private ExpenseEntity(Builder builder) {
        this.id = builder.id;
        this.uuid = builder.uuid;
        this.documentNumber = builder.documentNumber;
        this.providerId = builder.providerId;
        this.expenseDate = builder.expenseDate;
        this.amount = builder.amount;
        this.associatedDocument = builder.associatedDocument;
        this.expenseReasonId = builder.expenseReasonId;
        this.expenseStatusId = builder.expenseStatusId;
        this.expenseBulkUploadId = builder.expenseBulkUploadId;
        this.receiptId = builder.receiptId;
        this.currencyId = builder.currencyId;
    }

    public Long getId() {
        return id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public Long getProviderId() {
        return providerId;
    }

    public LocalDate getExpenseDate() {
        return expenseDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getAssociatedDocument() {
        return associatedDocument;
    }

    public Long getExpenseReasonId() {
        return expenseReasonId;
    }

    public Long getExpenseStatusId() {
        return expenseStatusId;
    }

    public void setExpenseStatusId(Long expenseStatusId) {
        this.expenseStatusId = expenseStatusId;
    }

    public Long getExpenseBulkUploadId() {
        return expenseBulkUploadId;
    }

    public Long getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(Long receiptId) {
        this.receiptId = receiptId;
    }

    public Long getCurrencyId() {
        return currencyId;
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
        private String documentNumber;
        private Long providerId;
        private LocalDate expenseDate;
        private BigDecimal amount;
        private String associatedDocument;
        private Long expenseReasonId;
        private Long expenseStatusId;
        private Long expenseBulkUploadId;
        private Long receiptId;
        private Long currencyId;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder uuid(UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder documentNumber(String documentNumber) {
            this.documentNumber = documentNumber;
            return this;
        }

        public Builder providerId(Long providerId) {
            this.providerId = providerId;
            return this;
        }

        public Builder expenseDate(LocalDate expenseDate) {
            this.expenseDate = expenseDate;
            return this;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder associatedDocument(String associatedDocument) {
            this.associatedDocument = associatedDocument;
            return this;
        }

        public Builder expenseReasonId(Long expenseReasonId) {
            this.expenseReasonId = expenseReasonId;
            return this;
        }

        public Builder expenseStatusId(Long expenseStatusId) {
            this.expenseStatusId = expenseStatusId;
            return this;
        }

        public Builder expenseBulkUploadId(Long expenseBulkUploadId) {
            this.expenseBulkUploadId = expenseBulkUploadId;
            return this;
        }

        public Builder receiptId(Long receiptId) {
            this.receiptId = receiptId;
            return this;
        }

        public Builder currencyId(Long currencyId) {
            this.currencyId = currencyId;
            return this;
        }

        public ExpenseEntity build() {
            return new ExpenseEntity(this);
        }
    }
}
