package com.cibertec.sga.payment.web.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.UUID;

/**
 * Cuentas por cobrar seleccionadas para calcular el total (RF-22) o confirmar el pago (RF-23).
 */
public record ProcessPaymentRequest(
    @NotEmpty(message = "Debe seleccionar al menos una cuenta por cobrar")
    List<UUID> accountReceivableUuids
) {
}
