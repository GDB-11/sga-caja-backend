package com.cibertec.sga.stall.application;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Datos de entrada para crear/editar un puesto.
 */
public record StallCommand(
    String number,
    UUID businessTypeUuid,
    UUID memberUuid,
    String tenantName,
    String tenantDocument,
    LocalDate validityStartDate,
    LocalDate validityEndDate
) {
}
