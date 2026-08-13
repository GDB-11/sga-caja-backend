package com.cibertec.sga.accountreceivable.application;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Datos de entrada para generar cuentas por cobrar de un servicio para todos los puestos
 * activos (RF-16).
 */
public record GenerateByStallCommand(
    UUID serviceUuid, LocalDate periodStartDate, LocalDate periodEndDate, BigDecimal amount
) {
}
