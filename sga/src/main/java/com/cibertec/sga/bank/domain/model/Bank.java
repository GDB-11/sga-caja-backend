package com.cibertec.sga.bank.domain.model;

import com.cibertec.sga.currency.domain.model.Currency;
import java.util.UUID;

/**
 * Banco: cuenta bancaria disponible para depósitos y canjes (RF-12).
 */
public final class Bank {

    private final UUID uuid;
    private final String name;
    private final String accountNumber;
    private final String cci;
    private final Currency currency;
    private final boolean active;

    private Bank(Builder builder) {
        this.uuid = builder.uuid;
        this.name = builder.name;
        this.accountNumber = builder.accountNumber;
        this.cci = builder.cci;
        this.currency = builder.currency;
        this.active = builder.active;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getCci() {
        return cci;
    }

    public Currency getCurrency() {
        return currency;
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
        private String accountNumber;
        private String cci;
        private Currency currency;
        private boolean active = true;

        public Builder uuid(UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder accountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
            return this;
        }

        public Builder cci(String cci) {
            this.cci = cci;
            return this;
        }

        public Builder currency(Currency currency) {
            this.currency = currency;
            return this;
        }

        public Builder active(boolean active) {
            this.active = active;
            return this;
        }

        public Bank build() {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("El nombre del banco es obligatorio");
            }
            if (accountNumber == null || accountNumber.isBlank()) {
                throw new IllegalArgumentException("El número de cuenta es obligatorio");
            }
            if (cci == null || cci.isBlank()) {
                throw new IllegalArgumentException("El CCI es obligatorio");
            }
            if (currency == null) {
                throw new IllegalArgumentException("La moneda del banco es obligatoria");
            }
            return new Bank(this);
        }
    }
}
