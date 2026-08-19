package com.cibertec.sga.expense.infrastructure.persistence;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Proyección de solo lectura de {@code "Expense"} con proveedor, motivo, estado, comprobante
 * (si fue procesado) y lote de carga masiva (si vino de una carga masiva) resueltos vía JOIN,
 * para evitar N+1 al listar/obtener egresos.
 */
public interface ExpenseRow {

    UUID getUuid();

    String getDocumentNumber();

    UUID getProviderUuid();

    String getProviderName();

    String getProviderDocument();

    Boolean getProviderIsActive();

    LocalDate getExpenseDate();

    BigDecimal getAmount();

    String getAssociatedDocument();

    UUID getExpenseReasonUuid();

    String getExpenseReasonName();

    UUID getStatusUuid();

    String getStatusName();

    UUID getReceiptUuid();

    Long getReceiptCorrelativeNumber();

    LocalDate getReceiptIssueDate();

    UUID getReceiptTypeUuid();

    String getReceiptTypeName();

    UUID getBulkUploadUuid();

    String getBulkUploadFileName();

    UUID getCreatedByUuid();

    String getCreatedByUsername();

    UUID getCurrencyUuid();

    String getCurrencyCode();

    String getCurrencyName();
}
