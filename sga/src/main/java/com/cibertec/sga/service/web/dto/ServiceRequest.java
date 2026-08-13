package com.cibertec.sga.service.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;

public record ServiceRequest(
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
    String name,

    @NotNull(message = "La recurrencia es obligatoria")
    UUID recurrenceTypeUuid,

    @NotNull(message = "El destino de cobro es obligatorio")
    UUID chargeTargetTypeUuid,

    @NotNull(message = "La moneda es obligatoria")
    UUID currencyUuid,

    boolean consumptionBased,

    @DecimalMin(value = "0.0", inclusive = false, message = "El costo debe ser mayor a cero")
    BigDecimal cost,

    @DecimalMin(value = "0.0", inclusive = false, message = "El costo unitario debe ser mayor a cero")
    BigDecimal unitCost
) {
}
