package com.cibertec.sga.service.application;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Datos de entrada para crear/editar un servicio.
 */
public record ServiceCommand(
    String name,
    UUID recurrenceTypeUuid,
    UUID chargeTargetTypeUuid,
    UUID currencyUuid,
    boolean consumptionBased,
    BigDecimal cost,
    BigDecimal unitCost
) {
}
