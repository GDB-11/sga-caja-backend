package com.cibertec.sga.currency.domain.model;

import java.util.UUID;

/**
 * Denominación monetaria usada por bancos, servicios y comprobantes (RF-12, RF-14).
 */
public final class Currency {

    private final UUID uuid;
    private final String code;
    private final String name;

    private Currency(Builder builder) {
        this.uuid = builder.uuid;
        this.code = builder.code;
        this.name = builder.name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private UUID uuid;
        private String code;
        private String name;

        public Builder uuid(UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder code(String code) {
            this.code = code;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Currency build() {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("El nombre de la moneda es obligatorio");
            }
            return new Currency(this);
        }
    }
}
