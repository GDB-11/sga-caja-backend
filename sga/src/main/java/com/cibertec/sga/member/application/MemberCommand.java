package com.cibertec.sga.member.application;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Datos de entrada para crear/editar un socio.
 */
public record MemberCommand(
    String code, String firstName, String lastName, String shareNumber, UUID stageUuid, LocalDate birthDate
) {
}
