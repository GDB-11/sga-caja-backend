package com.cibertec.sga.accountreceivable.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Datos para generar cuentas por cobrar de un servicio para socios activos, filtrables por
 * etapa y con opción de limitar repetidos por nombre y apellido (RF-18, RN-06). {@code amount}
 * es obligatorio para servicios de costo fijo y debe omitirse para servicios por consumo (el
 * monto se calcula al registrar las lecturas, RF-17).
 */
public record GenerateByMemberRequest(
    @NotNull(message = "El servicio es obligatorio")
    UUID serviceUuid,

    @NotNull(message = "La fecha de inicio del período es obligatoria")
    LocalDate periodStartDate,

    @NotNull(message = "La fecha de fin del período es obligatoria")
    LocalDate periodEndDate,

    @DecimalMin(value = "0.0", inclusive = false, message = "El monto debe ser mayor a cero")
    BigDecimal amount,

    @NotEmpty(message = "Debe indicar al menos una etapa")
    List<Short> stageCodes,

    boolean uniqueMembers
) {
}
