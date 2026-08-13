package com.cibertec.sga.income.domain.model;

import com.cibertec.sga.incomecategory.domain.model.IncomeCategory;
import com.cibertec.sga.receipt.domain.model.Receipt;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Ingreso externo con depositante, categoría, concepto y monto (RF-25), emitido con su
 * {@link Receipt} de tipo {@code Income}.
 */
public final class Income {

    private final UUID uuid;
    private final Receipt receipt;
    private final String depositorName;
    private final IncomeCategory incomeCategory;
    private final String concept;
    private final BigDecimal amount;

    private Income(Builder builder) {
        this.uuid = builder.uuid;
        this.receipt = builder.receipt;
        this.depositorName = builder.depositorName;
        this.incomeCategory = builder.incomeCategory;
        this.concept = builder.concept;
        this.amount = builder.amount;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Receipt getReceipt() {
        return receipt;
    }

    public String getDepositorName() {
        return depositorName;
    }

    public IncomeCategory getIncomeCategory() {
        return incomeCategory;
    }

    public String getConcept() {
        return concept;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private UUID uuid;
        private Receipt receipt;
        private String depositorName;
        private IncomeCategory incomeCategory;
        private String concept;
        private BigDecimal amount;

        public Builder uuid(UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder receipt(Receipt receipt) {
            this.receipt = receipt;
            return this;
        }

        public Builder depositorName(String depositorName) {
            this.depositorName = depositorName;
            return this;
        }

        public Builder incomeCategory(IncomeCategory incomeCategory) {
            this.incomeCategory = incomeCategory;
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

        public Income build() {
            if (receipt == null) {
                throw new IllegalArgumentException("El comprobante del ingreso es obligatorio");
            }
            if (depositorName == null || depositorName.isBlank()) {
                throw new IllegalArgumentException("El nombre del depositante es obligatorio");
            }
            if (incomeCategory == null) {
                throw new IllegalArgumentException("La categoría del ingreso es obligatoria");
            }
            if (concept == null || concept.isBlank()) {
                throw new IllegalArgumentException("El concepto del ingreso es obligatorio");
            }
            if (amount == null || amount.signum() <= 0) {
                throw new IllegalArgumentException("El monto del ingreso debe ser mayor a cero");
            }
            return new Income(this);
        }
    }
}
