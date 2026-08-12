package com.cibertec.sga.chargetargettype.domain.model;

import java.util.UUID;

/**
 * Destino de cobro de un servicio ("Cargo a": Member o Stall) (RF-14, RN-02).
 */
public final class ChargeTargetType {

    private final UUID uuid;
    private final String name;

    private ChargeTargetType(Builder builder) {
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

        public ChargeTargetType build() {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("El nombre del destino de cobro es obligatorio");
            }
            return new ChargeTargetType(this);
        }
    }
}
