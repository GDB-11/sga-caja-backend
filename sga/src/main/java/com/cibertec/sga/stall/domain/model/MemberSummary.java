package com.cibertec.sga.stall.domain.model;

import java.util.UUID;

/**
 * Referencia mínima al socio inquilino de un puesto (RN-01) — evita que {@code Stall} dependa
 * del modelo de dominio completo de {@code Member}.
 */
public record MemberSummary(UUID uuid, String fullName) {
}
