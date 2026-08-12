package com.cibertec.sga.user.domain.model;

import java.util.UUID;

/**
 * Usuario que inicia sesión y opera el sistema (RF-01-RF-04). {@code roleUuid}/{@code roleName}
 * vienen desnormalizados desde {@code Role} vía join en infraestructura: se leen en cada
 * autenticación/autorización, así que evitar una consulta adicional por request importa más
 * aquí que en un catálogo administrado por CRUD.
 */
public final class User {

    private final UUID uuid;
    private final String username;
    private final String passwordHash;
    private final String firstName;
    private final String lastName;
    private final UUID roleUuid;
    private final String roleName;
    private final boolean active;

    private User(Builder builder) {
        this.uuid = builder.uuid;
        this.username = builder.username;
        this.passwordHash = builder.passwordHash;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.roleUuid = builder.roleUuid;
        this.roleName = builder.roleName;
        this.active = builder.active;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public UUID getRoleUuid() {
        return roleUuid;
    }

    public String getRoleName() {
        return roleName;
    }

    public boolean isActive() {
        return active;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private UUID uuid;
        private String username;
        private String passwordHash;
        private String firstName;
        private String lastName;
        private UUID roleUuid;
        private String roleName;
        private boolean active;

        public Builder uuid(UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder passwordHash(String passwordHash) {
            this.passwordHash = passwordHash;
            return this;
        }

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder roleUuid(UUID roleUuid) {
            this.roleUuid = roleUuid;
            return this;
        }

        public Builder roleName(String roleName) {
            this.roleName = roleName;
            return this;
        }

        public Builder active(boolean active) {
            this.active = active;
            return this;
        }

        public User build() {
            if (username == null || username.isBlank()) {
                throw new IllegalArgumentException("El nombre de usuario es obligatorio");
            }
            return new User(this);
        }
    }
}
