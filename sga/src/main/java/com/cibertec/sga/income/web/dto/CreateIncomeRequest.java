package com.cibertec.sga.income.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Datos para registrar un ingreso externo (RF-25).
 */
public record CreateIncomeRequest(
    @NotBlank(message = "El nombre del depositante es obligatorio")
    String depositorName,

    @NotNull(message = "La categoría de ingreso es obligatoria")
    UUID incomeCategoryUuid,

    @NotNull(message = "La moneda es obligatoria")
    UUID currencyUuid,

    @NotBlank(message = "El concepto es obligatorio")
    String concept,

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El monto debe ser mayor a cero")
    BigDecimal amount
) {
}
