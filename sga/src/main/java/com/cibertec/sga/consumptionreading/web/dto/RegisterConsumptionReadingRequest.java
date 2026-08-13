package com.cibertec.sga.consumptionreading.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record RegisterConsumptionReadingRequest(
    @NotNull(message = "La cuenta por cobrar es obligatoria")
    UUID accountReceivableUuid,

    @NotNull(message = "La lectura inicial es obligatoria")
    @DecimalMin(value = "0.0", message = "La lectura inicial debe ser mayor o igual a cero")
    BigDecimal initialReading,

    @NotNull(message = "La lectura final es obligatoria")
    @DecimalMin(value = "0.0", message = "La lectura final debe ser mayor o igual a cero")
    BigDecimal finalReading
) {
}
