package com.cibertec.sga.businesstype.domain.model;

import java.util.UUID;

/**
 * Giro comercial ("BusinessType") al que puede estar asociado un puesto (RF-08, RN-01).
 */
public final class BusinessType {

    private final UUID uuid;
    private final String name;

    private BusinessType(Builder builder) {
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

        public BusinessType build() {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("El nombre del giro comercial es obligatorio");
            }
            return new BusinessType(this);
        }
    }
}
