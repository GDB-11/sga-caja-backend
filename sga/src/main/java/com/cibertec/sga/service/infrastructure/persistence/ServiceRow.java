package com.cibertec.sga.service.infrastructure.persistence;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Proyección de solo lectura de {@code "Service"} con recurrencia, destino de cobro y moneda
 * resueltos vía JOIN, para evitar N+1 al listar/obtener servicios.
 */
public interface ServiceRow {

    UUID getUuid();

    String getName();

    UUID getRecurrenceTypeUuid();

    String getRecurrenceTypeName();

    UUID getChargeTargetTypeUuid();

    String getChargeTargetTypeName();

    UUID getCurrencyUuid();

    String getCurrencyCode();

    String getCurrencyName();

    Boolean getIsConsumptionBased();

    BigDecimal getCost();

    BigDecimal getUnitCost();

    Boolean getIsActive();
}
