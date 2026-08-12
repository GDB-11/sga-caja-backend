package com.cibertec.sga.role.domain.model;

import java.util.UUID;

/**
 * Rol del sistema ("Administrator"/"CashierOperator") usado para autorización (RF-01-RF-04).
 */
public final class Role {

    private final UUID uuid;
    private final String name;

    private Role(Builder builder) {
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

        public Role build() {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("El nombre del rol es obligatorio");
            }
            return new Role(this);
        }
    }
}
