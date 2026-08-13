package com.cibertec.sga.consumptionreading.infrastructure.persistence;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Proyección de solo lectura de {@code "ConsumptionReading"} con el Uuid de la cuenta por
 * cobrar resuelto vía JOIN, para evitar N+1 al obtener lecturas.
 */
public interface ConsumptionReadingRow {

    UUID getUuid();

    UUID getAccountReceivableUuid();

    BigDecimal getInitialReading();

    BigDecimal getFinalReading();

    BigDecimal getUnitCost();

    BigDecimal getCalculatedAmount();
}
