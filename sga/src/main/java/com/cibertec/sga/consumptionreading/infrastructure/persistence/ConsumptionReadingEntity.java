package com.cibertec.sga.consumptionreading.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Fila de la tabla {@code "ConsumptionReading"}. {@code Uuid}/{@code CalculatedAmount} son
 * columnas con valor calculado por la base de datos ({@code CalculatedAmount} es una columna
 * generada: {@code GREATEST(FinalReading - InitialReading, 0) * UnitCost}); Hibernate las
 * recupera tras el insert.
 */
@Entity
@Table(name = "ConsumptionReading")
@EntityListeners(AuditingEntityListener.class)
public class ConsumptionReadingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Generated(event = EventType.INSERT)
    @Column(name = "Uuid", insertable = false, updatable = false)
    private UUID uuid;

    @Column(name = "AccountReceivableId", nullable = false)
    private Long accountReceivableId;

    @Column(name = "InitialReading", nullable = false)
    private BigDecimal initialReading;

    @Column(name = "FinalReading", nullable = false)
    private BigDecimal finalReading;

    @Column(name = "UnitCost", nullable = false)
    private BigDecimal unitCost;

    @Generated(event = EventType.INSERT)
    @Column(name = "CalculatedAmount", insertable = false, updatable = false)
    private BigDecimal calculatedAmount;

    @CreatedDate
    @Column(name = "CreatedAt", nullable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "UpdatedAt", nullable = false)
    private Instant updatedAt;

    protected ConsumptionReadingEntity() {
    }

    private ConsumptionReadingEntity(Builder builder) {
        this.id = builder.id;
        this.uuid = builder.uuid;
        this.accountReceivableId = builder.accountReceivableId;
        this.initialReading = builder.initialReading;
        this.finalReading = builder.finalReading;
        this.unitCost = builder.unitCost;
    }

    public Long getId() {
        return id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Long getAccountReceivableId() {
        return accountReceivableId;
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Long id;
        private UUID uuid;
        private Long accountReceivableId;
        private BigDecimal initialReading;
        private BigDecimal finalReading;
        private BigDecimal unitCost;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder uuid(UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder accountReceivableId(Long accountReceivableId) {
            this.accountReceivableId = accountReceivableId;
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

        public ConsumptionReadingEntity build() {
            return new ConsumptionReadingEntity(this);
        }
    }
}
