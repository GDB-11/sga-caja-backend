package com.cibertec.sga.accountreceivable.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Datos para generar cuentas por cobrar de un servicio para todos los puestos activos (RF-16).
 * {@code amount} es obligatorio para servicios de costo fijo y debe omitirse para servicios por
 * consumo (el monto se calcula al registrar las lecturas, RF-17).
 */
public record GenerateByStallRequest(
    @NotNull(message = "El servicio es obligatorio")
    UUID serviceUuid,

    @NotNull(message = "La fecha de inicio del período es obligatoria")
    LocalDate periodStartDate,

    @NotNull(message = "La fecha de fin del período es obligatoria")
    LocalDate periodEndDate,

    @DecimalMin(value = "0.0", inclusive = false, message = "El monto debe ser mayor a cero")
    BigDecimal amount
) {
}
