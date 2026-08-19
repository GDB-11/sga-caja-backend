package com.cibertec.sga.expense.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Datos para registrar un egreso individual (RF-27).
 */
public record RegisterExpenseRequest(
    @NotBlank(message = "El número de documento es obligatorio")
    String documentNumber,

    @NotNull(message = "El proveedor es obligatorio")
    UUID providerUuid,

    @NotNull(message = "La fecha de egreso es obligatoria")
    LocalDate expenseDate,

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El monto debe ser mayor a cero")
    BigDecimal amount,

    String associatedDocument,

    @NotNull(message = "El motivo del egreso es obligatorio")
    UUID expenseReasonUuid,

    @NotNull(message = "La moneda es obligatoria")
    UUID currencyUuid
) {
}
