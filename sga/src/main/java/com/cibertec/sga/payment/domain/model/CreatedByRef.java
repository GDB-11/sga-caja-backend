package com.cibertec.sga.payment.domain.model;

import java.util.UUID;

/**
 * Referencia mínima al usuario que registró un {@link Payment} (RNF-14) — evita que
 * {@code Payment} dependa del módulo {@code user} para el detalle completo, que no lo necesita.
 */
public record CreatedByRef(UUID uuid, String username) {
}
