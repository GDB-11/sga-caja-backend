package com.cibertec.sga.member.infrastructure.persistence;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Proyección de solo lectura de {@code "Member"} con la etapa resuelta vía JOIN, para evitar
 * N+1 al listar/obtener socios (el modelo de dominio {@link com.cibertec.sga.member.domain.model.Member}
 * es {@code Uuid}-only y necesita el nombre de la etapa referenciada, no solo su Id interno).
 */
public interface MemberRow {

    UUID getUuid();

    String getCode();

    String getFirstName();

    String getLastName();

    String getShareNumber();

    UUID getStageUuid();

    Short getStageCode();

    String getStageName();

    LocalDate getBirthDate();

    Boolean getIsActive();

    Instant getCreatedAt();

    Instant getUpdatedAt();
}
