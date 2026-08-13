package com.cibertec.sga.receipttype.domain.model;

import java.util.UUID;

/**
 * Tipo de comprobante correlativo (Income, Expense, BankTransaction) (RN-04).
 */
public final class ReceiptType {

    private final UUID uuid;
    private final String name;

    private ReceiptType(Builder builder) {
        this.uuid = builder.uuid;
        this.name = builder.name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private UUID uuid;
        private String name;

        public Builder uuid(UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public ReceiptType build() {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("El nombre del tipo de comprobante es obligatorio");
            }
            return new ReceiptType(this);
        }
    }
}
