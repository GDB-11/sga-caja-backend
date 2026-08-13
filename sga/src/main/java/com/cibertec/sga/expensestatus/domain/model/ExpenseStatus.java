package com.cibertec.sga.expensestatus.domain.model;

import java.util.UUID;

/**
 * Estado de ciclo de vida de un comprobante de egreso (RF-30).
 */
public final class ExpenseStatus {

    private final UUID uuid;
    private final String name;

    private ExpenseStatus(Builder builder) {
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

        public ExpenseStatus build() {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("El nombre del estado de egreso es obligatorio");
            }
            return new ExpenseStatus(this);
        }
    }
}
