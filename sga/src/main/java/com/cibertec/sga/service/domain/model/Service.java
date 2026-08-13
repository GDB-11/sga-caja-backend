package com.cibertec.sga.service.domain.model;

import com.cibertec.sga.chargetargettype.domain.model.ChargeTargetType;
import com.cibertec.sga.currency.domain.model.Currency;
import com.cibertec.sga.recurrencetype.domain.model.RecurrenceType;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Servicio: concepto cobrable, de costo fijo o dependiente de consumo (RF-13–RF-15).
 */
public final class Service {

    private final UUID uuid;
    private final String name;
    private final RecurrenceType recurrenceType;
    private final ChargeTargetType chargeTargetType;
    private final Currency currency;
    private final boolean consumptionBased;
    private final BigDecimal cost;
    private final BigDecimal unitCost;
    private final boolean active;

    private Service(Builder builder) {
        this.uuid = builder.uuid;
        this.name = builder.name;
        this.recurrenceType = builder.recurrenceType;
        this.chargeTargetType = builder.chargeTargetType;
        this.currency = builder.currency;
        this.consumptionBased = builder.consumptionBased;
        this.cost = builder.cost;
        this.unitCost = builder.unitCost;
        this.active = builder.active;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public RecurrenceType getRecurrenceType() {
        return recurrenceType;
    }

    public ChargeTargetType getChargeTargetType() {
        return chargeTargetType;
    }

    public Currency getCurrency() {
        return currency;
    }

    public boolean isConsumptionBased() {
        return consumptionBased;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public BigDecimal getUnitCost() {
        return unitCost;
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
        private RecurrenceType recurrenceType;
        private ChargeTargetType chargeTargetType;
        private Currency currency;
        private boolean consumptionBased;
        private BigDecimal cost;
        private BigDecimal unitCost;
        private boolean active = true;

        public Builder uuid(UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder recurrenceType(RecurrenceType recurrenceType) {
            this.recurrenceType = recurrenceType;
            return this;
        }

        public Builder chargeTargetType(ChargeTargetType chargeTargetType) {
            this.chargeTargetType = chargeTargetType;
            return this;
        }

        public Builder currency(Currency currency) {
            this.currency = currency;
            return this;
        }

        public Builder consumptionBased(boolean consumptionBased) {
            this.consumptionBased = consumptionBased;
            return this;
        }

        public Builder cost(BigDecimal cost) {
            this.cost = cost;
            return this;
        }

        public Builder unitCost(BigDecimal unitCost) {
            this.unitCost = unitCost;
            return this;
        }

        public Builder active(boolean active) {
            this.active = active;
            return this;
        }

        public Service build() {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("El nombre del servicio es obligatorio");
            }
            if (recurrenceType == null) {
                throw new IllegalArgumentException("La recurrencia del servicio es obligatoria");
            }
            if (chargeTargetType == null) {
                throw new IllegalArgumentException("El destino de cobro del servicio es obligatorio");
            }
            if (currency == null) {
                throw new IllegalArgumentException("La moneda del servicio es obligatoria");
            }
            if (consumptionBased) {
                if (unitCost == null || cost != null) {
                    throw new IllegalArgumentException(
                        "Un servicio por consumo debe tener costo unitario y no debe tener costo fijo"
                    );
                }
            } else {
                if (cost == null || unitCost != null) {
                    throw new IllegalArgumentException(
                        "Un servicio de costo fijo debe tener costo y no debe tener costo unitario"
                    );
                }
            }
            return new Service(this);
        }
    }
}
