package com.cibertec.sga.accountreceivable.domain.model;

import java.util.UUID;

/**
 * Referencia mínima al puesto destinatario de una cuenta por cobrar (RN-02) — evita que
 * {@code AccountReceivable} dependa del modelo de dominio completo de {@code Stall}.
 */
public record StallRef(UUID uuid, String number) {
}
