package com.cibertec.sga.consumptionreading.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Lectura inicial/final de un servicio por consumo, asociada 1:1 a una cuenta por cobrar
 * (RF-17, RN-05). {@code calculatedAmount} lo calcula la base de datos (columna generada
 * {@code GREATEST(FinalReading - InitialReading, 0) * UnitCost}) — la aplicación nunca
 * recalcula la fórmula, solo la lee de vuelta tras el insert.
 */
public final class ConsumptionReading {

    private final UUID uuid;
    private final UUID accountReceivableUuid;
    private final BigDecimal initialReading;
    private final BigDecimal finalReading;
    private final BigDecimal unitCost;
    private final BigDecimal calculatedAmount;

    private ConsumptionReading(Builder builder) {
        this.uuid = builder.uuid;
        this.accountReceivableUuid = builder.accountReceivableUuid;
        this.initialReading = builder.initialReading;
        this.finalReading = builder.finalReading;
        this.unitCost = builder.unitCost;
        this.calculatedAmount = builder.calculatedAmount;
    }

    public UUID getUuid() {
        return uuid;
    }

    public UUID getAccountReceivableUuid() {
        return accountReceivableUuid;
    }

    public BigDecimal getInitialReading() {
        return initialReading;
    }

    public BigDecimal getFinalReading() {
        return finalReading;
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public BigDecimal getCalculatedAmount() {
        return calculatedAmount;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private UUID uuid;
        private UUID accountReceivableUuid;
        private BigDecimal initialReading;
        private BigDecimal finalReading;
        private BigDecimal unitCost;
        private BigDecimal calculatedAmount;

        public Builder uuid(UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder accountReceivableUuid(UUID accountReceivableUuid) {
            this.accountReceivableUuid = accountReceivableUuid;
            return this;
        }

        public Builder initialReading(BigDecimal initialReading) {
            this.initialReading = initialReading;
            return this;
        }

        public Builder finalReading(BigDecimal finalReading) {
            this.finalReading = finalReading;
            return this;
        }

        public Builder unitCost(BigDecimal unitCost) {
            this.unitCost = unitCost;
            return this;
        }

        public Builder calculatedAmount(BigDecimal calculatedAmount) {
            this.calculatedAmount = calculatedAmount;
            return this;
        }

        public ConsumptionReading build() {
            if (accountReceivableUuid == null) {
                throw new IllegalArgumentException("La cuenta por cobrar es obligatoria");
            }
            if (initialReading == null || initialReading.signum() < 0) {
                throw new IllegalArgumentException("La lectura inicial debe ser mayor o igual a cero");
            }
            if (finalReading == null || finalReading.signum() < 0) {
                throw new IllegalArgumentException("La lectura final debe ser mayor o igual a cero");
            }
            if (unitCost == null || unitCost.signum() <= 0) {
                throw new IllegalArgumentException("El costo unitario debe ser mayor a cero");
            }
            return new ConsumptionReading(this);
        }
    }
}
