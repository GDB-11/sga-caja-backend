package com.cibertec.sga.expense.domain.model;

import com.cibertec.sga.currency.domain.model.Currency;
import com.cibertec.sga.expensereason.domain.model.ExpenseReason;
import com.cibertec.sga.expensestatus.domain.model.ExpenseStatus;
import com.cibertec.sga.provider.domain.model.Provider;
import com.cibertec.sga.receipt.domain.model.Receipt;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Egreso individual o proveniente de una carga masiva (RF-27–RF-30). {@code receipt} queda
 * {@code null} hasta que el egreso se procesa (RF-30, "procesar"); {@code bulkUpload} queda
 * {@code null} si se registró individualmente (RF-27).
 */
public final class Expense {

    private final UUID uuid;
    private final String documentNumber;
    private final Provider provider;
    private final LocalDate expenseDate;
    private final BigDecimal amount;
    private final String associatedDocument;
    private final ExpenseReason expenseReason;
    private final ExpenseStatus status;
    private final Receipt receipt;
    private final ExpenseBulkUploadRef bulkUpload;
    private final CreatedByRef createdBy;
    private final Currency currency;

    private Expense(Builder builder) {
        this.uuid = builder.uuid;
        this.documentNumber = builder.documentNumber;
        this.provider = builder.provider;
        this.expenseDate = builder.expenseDate;
        this.amount = builder.amount;
        this.associatedDocument = builder.associatedDocument;
        this.expenseReason = builder.expenseReason;
        this.status = builder.status;
        this.receipt = builder.receipt;
        this.bulkUpload = builder.bulkUpload;
        this.currency = builder.currency;
        this.createdBy = builder.createdBy;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public Provider getProvider() {
        return provider;
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

    public ExpenseReason getExpenseReason() {
        return expenseReason;
    }

    public ExpenseStatus getStatus() {
        return status;
    }

    public Receipt getReceipt() {
        return receipt;
    }

    public ExpenseBulkUploadRef getBulkUpload() {
        return bulkUpload;
    }

    public CreatedByRef getCreatedBy() {
        return createdBy;
    }

    public Currency getCurrency() {
        return currency;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private UUID uuid;
        private String documentNumber;
        private Provider provider;
        private LocalDate expenseDate;
        private BigDecimal amount;
        private String associatedDocument;
        private ExpenseReason expenseReason;
        private ExpenseStatus status;
        private Receipt receipt;
        private ExpenseBulkUploadRef bulkUpload;
        private CreatedByRef createdBy;
        private Currency currency;

        public Builder uuid(UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder documentNumber(String documentNumber) {
            this.documentNumber = documentNumber;
            return this;
        }

        public Builder provider(Provider provider) {
            this.provider = provider;
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

        public Builder expenseReason(ExpenseReason expenseReason) {
            this.expenseReason = expenseReason;
            return this;
        }

        public Builder status(ExpenseStatus status) {
            this.status = status;
            return this;
        }

        public Builder receipt(Receipt receipt) {
            this.receipt = receipt;
            return this;
        }

        public Builder bulkUpload(ExpenseBulkUploadRef bulkUpload) {
            this.bulkUpload = bulkUpload;
            return this;
        }

        public Builder createdBy(CreatedByRef createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public Builder currency(Currency currency) {
            this.currency = currency;
            return this;
        }

        public Expense build() {
            if (documentNumber == null || documentNumber.isBlank()) {
                throw new IllegalArgumentException("El número de documento es obligatorio");
            }
            if (provider == null) {
                throw new IllegalArgumentException("El proveedor es obligatorio");
            }
            if (expenseDate == null) {
                throw new IllegalArgumentException("La fecha del egreso es obligatoria");
            }
            if (amount == null || amount.signum() <= 0) {
                throw new IllegalArgumentException("El monto del egreso debe ser mayor a cero");
            }
            if (expenseReason == null) {
                throw new IllegalArgumentException("El motivo del egreso es obligatorio");
            }
            if (status == null) {
                throw new IllegalArgumentException("El estado del egreso es obligatorio");
            }
            if (currency == null) {
                throw new IllegalArgumentException("La moneda del egreso es obligatoria");
            }
            return new Expense(this);
        }
    }
}
