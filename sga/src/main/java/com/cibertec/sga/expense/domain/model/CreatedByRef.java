package com.cibertec.sga.expense.domain.model;

import java.util.UUID;

/**
 * Referencia mínima al usuario que registró un {@link Expense} (RNF-14) — evita que
 * {@code Expense} dependa del módulo {@code user} para el detalle completo, que no lo necesita.
 */
public record CreatedByRef(UUID uuid, String username) {
}
