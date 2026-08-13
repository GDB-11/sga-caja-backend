package com.cibertec.sga.accountreceivablestatus.domain.model;

import java.util.UUID;

/**
 * Estado del ciclo de vida de una cuenta por cobrar (RN-03): Pending, Paid, Exempt.
 */
public final class AccountReceivableStatus {

    private final UUID uuid;
    private final String name;

    private AccountReceivableStatus(Builder builder) {
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

        public AccountReceivableStatus build() {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("El nombre del estado es obligatorio");
            }
            return new AccountReceivableStatus(this);
        }
    }
}
