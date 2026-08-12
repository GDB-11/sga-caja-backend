package com.cibertec.sga.provider.domain.model;

import java.util.UUID;

/**
 * Proveedor: vendedor asociado a un egreso (RF-27).
 */
public final class Provider {

    private final UUID uuid;
    private final String name;
    private final String document;
    private final boolean active;

    private Provider(Builder builder) {
        this.uuid = builder.uuid;
        this.name = builder.name;
        this.document = builder.document;
        this.active = builder.active;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getDocument() {
        return document;
    }

    public boolean isActive() {
        return active;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private UUID uuid;
        private String name;
        private String document;
        private boolean active = true;

        public Builder uuid(UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder document(String document) {
            this.document = document;
            return this;
        }

        public Builder active(boolean active) {
            this.active = active;
            return this;
        }

        public Provider build() {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("El nombre del proveedor es obligatorio");
            }
            return new Provider(this);
        }
    }
}
