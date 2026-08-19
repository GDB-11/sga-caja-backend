package com.cibertec.sga.accountreceivable.domain.model;

import com.cibertec.sga.accountreceivablestatus.domain.model.AccountReceivableStatus;
import com.cibertec.sga.currency.domain.model.Currency;
import com.cibertec.sga.service.domain.model.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Cuenta por cobrar: cargo de un {@link Service} a exactamente un {@link MemberRef} o un
 * {@link StallRef} para un período determinado (RF-16–RF-18, RN-01, RN-02, RN-03).
 */
public final class AccountReceivable {

    private final UUID uuid;
    private final Service service;
    private final MemberRef member;
    private final StallRef stall;
    private final LocalDate periodStartDate;
    private final LocalDate periodEndDate;
    private final BigDecimal amount;
    private final AccountReceivableStatus status;
    private final Currency currency;

    private AccountReceivable(Builder builder) {
        this.uuid = builder.uuid;
        this.service = builder.service;
        this.member = builder.member;
        this.stall = builder.stall;
        this.periodStartDate = builder.periodStartDate;
        this.periodEndDate = builder.periodEndDate;
        this.amount = builder.amount;
        this.status = builder.status;
        this.currency = builder.currency;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Service getService() {
        return service;
    }

    public MemberRef getMember() {
        return member;
    }

    public StallRef getStall() {
        return stall;
    }

    public LocalDate getPeriodStartDate() {
        return periodStartDate;
    }

    public LocalDate getPeriodEndDate() {
        return periodEndDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public AccountReceivableStatus getStatus() {
        return status;
    }

    public Currency getCurrency() {
        return currency;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private UUID uuid;
        private Service service;
        private MemberRef member;
        private StallRef stall;
        private LocalDate periodStartDate;
        private LocalDate periodEndDate;
        private BigDecimal amount;
        private AccountReceivableStatus status;
        private Currency currency;

        public Builder uuid(UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder service(Service service) {
            this.service = service;
            return this;
        }

        public Builder member(MemberRef member) {
            this.member = member;
            return this;
        }

        public Builder stall(StallRef stall) {
            this.stall = stall;
            return this;
        }

        public Builder periodStartDate(LocalDate periodStartDate) {
            this.periodStartDate = periodStartDate;
            return this;
        }

        public Builder periodEndDate(LocalDate periodEndDate) {
            this.periodEndDate = periodEndDate;
            return this;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder status(AccountReceivableStatus status) {
            this.status = status;
            return this;
        }

        public Builder currency(Currency currency) {
            this.currency = currency;
            return this;
        }

        public AccountReceivable build() {
            if (service == null) {
                throw new IllegalArgumentException("El servicio de la cuenta por cobrar es obligatorio");
            }
            if ((member == null) == (stall == null)) {
                throw new IllegalArgumentException("La cuenta por cobrar debe tener exactamente un socio o un puesto");
            }
            if (periodStartDate == null || periodEndDate == null) {
                throw new IllegalArgumentException("El período de la cuenta por cobrar es obligatorio");
            }
            if (periodEndDate.isBefore(periodStartDate)) {
                throw new IllegalArgumentException("La fecha de fin del período debe ser posterior o igual a la de inicio");
            }
            if (amount == null || amount.signum() < 0) {
                throw new IllegalArgumentException("El monto de la cuenta por cobrar debe ser mayor o igual a cero");
            }
            if (status == null) {
                throw new IllegalArgumentException("El estado de la cuenta por cobrar es obligatorio");
            }
            if (currency == null) {
                throw new IllegalArgumentException("La moneda de la cuenta por cobrar es obligatoria");
            }
            return new AccountReceivable(this);
        }
    }
}
