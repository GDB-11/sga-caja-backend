package com.cibertec.sga.accountreceivable.application;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Datos de entrada para generar cuentas por cobrar de un servicio para socios activos,
 * filtrables por etapa y con opción de limitar repetidos por nombre y apellido (RF-18, RN-06).
 */
public record GenerateByMemberCommand(
    UUID serviceUuid,
    LocalDate periodStartDate,
    LocalDate periodEndDate,
    BigDecimal amount,
    List<Short> stageCodes,
    boolean uniqueMembers
) {
}
