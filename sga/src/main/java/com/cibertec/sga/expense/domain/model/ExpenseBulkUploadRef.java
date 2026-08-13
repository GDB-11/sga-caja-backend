package com.cibertec.sga.expense.domain.model;

import java.util.UUID;

/**
 * Referencia mínima al lote de carga masiva que originó un {@link Expense} (RF-28) — evita que
 * {@code Expense} dependa de un módulo aparte para el detalle del lote, que no lo necesita.
 */
public record ExpenseBulkUploadRef(UUID uuid, String fileName) {
}
