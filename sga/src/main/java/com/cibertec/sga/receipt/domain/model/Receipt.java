package com.cibertec.sga.receipt.domain.model;

import com.cibertec.sga.currency.domain.model.Currency;
import com.cibertec.sga.receipttype.domain.model.ReceiptType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Comprobante correlativo emitido por una operación de ingreso, egreso o movimiento bancario
 * (RF-23–RF-31, RN-04, RNF-05). {@code correlativeNumber}/{@code issueDate} los asigna la base
 * de datos (trigger sobre la secuencia del {@link ReceiptType} e {@code IssueDate DEFAULT
 * CURRENT_DATE} respectivamente) — la aplicación nunca los calcula, solo los lee de vuelta tras
 * el insert.
 */
public final class Receipt {

    private final UUID uuid;
    private final ReceiptType receiptType;
    private final Long correlativeNumber;
    private final LocalDate issueDate;
    private final BigDecimal amount;
    private final String description;
    private final Currency currency;

    private Receipt(Builder builder) {
        this.uuid = builder.uuid;
        this.receiptType = builder.receiptType;
        this.correlativeNumber = builder.correlativeNumber;
        this.issueDate = builder.issueDate;
        this.amount = builder.amount;
        this.description = builder.description;
        this.currency = builder.currency;
    }

    public UUID getUuid() {
        return uuid;
    }

    public ReceiptType getReceiptType() {
        return receiptType;
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

    public Currency getCurrency() {
        return currency;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private UUID uuid;
        private ReceiptType receiptType;
        private Long correlativeNumber;
        private LocalDate issueDate;
        private BigDecimal amount;
        private String description;
        private Currency currency;

        public Builder uuid(UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder receiptType(ReceiptType receiptType) {
            this.receiptType = receiptType;
            return this;
        }

        public Builder correlativeNumber(Long correlativeNumber) {
            this.correlativeNumber = correlativeNumber;
            return this;
        }

        public Builder issueDate(LocalDate issueDate) {
            this.issueDate = issueDate;
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

        public Builder currency(Currency currency) {
            this.currency = currency;
            return this;
        }

        public Receipt build() {
            if (receiptType == null) {
                throw new IllegalArgumentException("El tipo de comprobante es obligatorio");
            }
            if (amount == null || amount.signum() <= 0) {
                throw new IllegalArgumentException("El monto del comprobante debe ser mayor a cero");
            }
            if (currency == null) {
                throw new IllegalArgumentException("La moneda del comprobante es obligatoria");
            }
            return new Receipt(this);
        }
    }
}
