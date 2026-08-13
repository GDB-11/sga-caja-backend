package com.cibertec.sga.accountreceivable.domain.model;

import java.util.UUID;

/**
 * Referencia mínima al socio destinatario de una cuenta por cobrar (RN-02) — evita que
 * {@code AccountReceivable} dependa del modelo de dominio completo de {@code Member}.
 */
public record MemberRef(UUID uuid, String fullName) {
}
