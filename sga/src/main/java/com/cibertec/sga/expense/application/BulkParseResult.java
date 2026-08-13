package com.cibertec.sga.expense.application;

import java.util.List;

/**
 * Resultado de leer un archivo de carga masiva de egresos (RF-28): filas leídas correctamente
 * más los errores de formato encontrados fila por fila (ej. fecha o monto no numérico) — no
 * incluye errores de validación de negocio (proveedor/motivo inexistente), que se resuelven
 * aparte en {@code ExpenseService} contra la base de datos.
 */
public record BulkParseResult(List<BulkExpenseRow> rows, List<String> errors) {
}
