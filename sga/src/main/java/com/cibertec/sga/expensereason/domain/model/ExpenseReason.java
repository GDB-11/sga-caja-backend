package com.cibertec.sga.expensereason.domain.model;

import java.util.UUID;

/**
 * Motivo de un egreso (RF-27).
 */
public final class ExpenseReason {

    private final UUID uuid;
    private final String name;

    private ExpenseReason(Builder builder) {
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

        public ExpenseReason build() {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("El nombre del motivo de egreso es obligatorio");
            }
            return new ExpenseReason(this);
        }
    }
}
