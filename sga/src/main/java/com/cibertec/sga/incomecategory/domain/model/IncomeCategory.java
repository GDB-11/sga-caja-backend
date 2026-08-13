package com.cibertec.sga.incomecategory.domain.model;

import java.util.UUID;

/**
 * Categoría de clasificación de un ingreso externo (RF-25).
 */
public final class IncomeCategory {

    private final UUID uuid;
    private final String name;

    private IncomeCategory(Builder builder) {
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

        public IncomeCategory build() {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("El nombre de la categoría de ingreso es obligatorio");
            }
            return new IncomeCategory(this);
        }
    }
}
