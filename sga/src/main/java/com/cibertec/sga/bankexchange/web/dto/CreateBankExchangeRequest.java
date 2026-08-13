package com.cibertec.sga.bankexchange.web.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Datos para canjear una cuenta por cobrar de un socio por una operación bancaria (RF-24).
 */
public record CreateBankExchangeRequest(
    @NotNull(message = "La cuenta por cobrar es obligatoria")
    UUID accountReceivableUuid,

    @NotNull(message = "El banco es obligatorio")
    UUID bankUuid,

    @NotNull(message = "La fecha de depósito es obligatoria")
    LocalDate depositDate
) {
}
