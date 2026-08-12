package com.cibertec.sga.stall.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.UUID;

public record StallRequest(
    @NotBlank(message = "El número de puesto es obligatorio")
    @Size(max = 20, message = "El número no puede superar los 20 caracteres")
    String number,

    @NotNull(message = "El giro comercial es obligatorio")
    UUID businessTypeUuid,

    UUID memberUuid,

    @Size(max = 150, message = "El nombre del inquilino no puede superar los 150 caracteres")
    String tenantName,

    @Size(max = 20, message = "El documento del inquilino no puede superar los 20 caracteres")
    String tenantDocument,

    LocalDate validityStartDate,

    LocalDate validityEndDate
) {
}
