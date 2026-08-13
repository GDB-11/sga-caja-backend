package com.cibertec.sga.bankexchange.domain.model;

import com.cibertec.sga.accountreceivable.domain.model.AccountReceivable;
import com.cibertec.sga.bank.domain.model.Bank;
import com.cibertec.sga.receipt.domain.model.Receipt;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Canje de una cuenta por cobrar de un socio por una operación bancaria (RF-24): la cuenta
 * queda liquidada mediante un depósito en un {@link Bank}, con su {@link Receipt} de tipo
 * {@code BankTransaction}.
 */
public final class BankExchange {

    private final UUID uuid;
    private final AccountReceivable accountReceivable;
    private final Bank bank;
    private final Receipt receipt;
    private final LocalDate depositDate;
    private final BigDecimal amount;

    private BankExchange(Builder builder) {
        this.uuid = builder.uuid;
        this.accountReceivable = builder.accountReceivable;
        this.bank = builder.bank;
        this.receipt = builder.receipt;
        this.depositDate = builder.depositDate;
        this.amount = builder.amount;
    }

    public UUID getUuid() {
        return uuid;
    }

    public AccountReceivable getAccountReceivable() {
        return accountReceivable;
    }

    public Bank getBank() {
        return bank;
    }

    public Receipt getReceipt() {
        return receipt;
    }

    public LocalDate getDepositDate() {
        return depositDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private UUID uuid;
        private AccountReceivable accountReceivable;
        private Bank bank;
        private Receipt receipt;
        private LocalDate depositDate;
        private BigDecimal amount;

        public Builder uuid(UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder accountReceivable(AccountReceivable accountReceivable) {
            this.accountReceivable = accountReceivable;
            return this;
        }

        public Builder bank(Bank bank) {
            this.bank = bank;
            return this;
        }

        public Builder receipt(Receipt receipt) {
            this.receipt = receipt;
            return this;
        }

        public Builder depositDate(LocalDate depositDate) {
            this.depositDate = depositDate;
            return this;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public BankExchange build() {
            if (accountReceivable == null) {
                throw new IllegalArgumentException("La cuenta por cobrar del canje es obligatoria");
            }
            if (bank == null) {
                throw new IllegalArgumentException("El banco del canje es obligatorio");
            }
            if (receipt == null) {
                throw new IllegalArgumentException("El comprobante del canje es obligatorio");
            }
            if (depositDate == null) {
                throw new IllegalArgumentException("La fecha de depósito es obligatoria");
            }
            if (amount == null || amount.signum() <= 0) {
                throw new IllegalArgumentException("El monto del canje debe ser mayor a cero");
            }
            return new BankExchange(this);
        }
    }
}
