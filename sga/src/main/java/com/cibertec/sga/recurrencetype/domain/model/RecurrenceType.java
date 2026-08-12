package com.cibertec.sga.recurrencetype.domain.model;

import java.util.UUID;

/**
 * Recurrencia de facturación de un servicio (RF-14): Monthly, Yearly, OneTime.
 */
public final class RecurrenceType {

    private final UUID uuid;
    private final String name;

    private RecurrenceType(Builder builder) {
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

        public RecurrenceType build() {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("El nombre de la recurrencia es obligatorio");
            }
            return new RecurrenceType(this);
        }
    }
}
