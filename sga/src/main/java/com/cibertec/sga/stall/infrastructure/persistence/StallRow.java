package com.cibertec.sga.stall.infrastructure.persistence;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Proyección de solo lectura de {@code "Stall"} con el giro comercial y el socio (si aplica)
 * resueltos vía JOIN, para evitar N+1 al listar/obtener puestos.
 */
public interface StallRow {

    UUID getUuid();

    String getNumber();

    UUID getBusinessTypeUuid();

    String getBusinessTypeName();

    UUID getMemberUuid();

    String getMemberFullName();

    String getTenantName();

    String getTenantDocument();

    LocalDate getValidityStartDate();

    LocalDate getValidityEndDate();

    Boolean getIsActive();
}
