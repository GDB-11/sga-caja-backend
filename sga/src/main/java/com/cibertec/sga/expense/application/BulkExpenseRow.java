package com.cibertec.sga.expense.application;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Fila cruda leída de un archivo de carga masiva de egresos (RF-28), antes de validar sus
 * referencias (proveedor, motivo) contra la base de datos.
 */
public record BulkExpenseRow(
    int rowNumber,
    String documentNumber,
    String providerName,
    LocalDate expenseDate,
    BigDecimal amount,
    String associatedDocument,
    String expenseReasonName
) {
}
