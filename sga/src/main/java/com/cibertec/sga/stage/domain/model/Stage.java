package com.cibertec.sga.stage.domain.model;

import java.util.UUID;

/**
 * Etapa de socio, usada para filtrar la generación de cuentas cobrables (RF-18, RN-06).
 */
public final class Stage {

    private final UUID uuid;
    private final short code;
    private final String name;

    private Stage(Builder builder) {
        this.uuid = builder.uuid;
        this.code = builder.code;
        this.name = builder.name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public short getCode() {
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
        private short code;
        private String name;

        public Builder uuid(UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder code(short code) {
            this.code = code;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Stage build() {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("El nombre de la etapa es obligatorio");
            }
            return new Stage(this);
        }
    }
}
