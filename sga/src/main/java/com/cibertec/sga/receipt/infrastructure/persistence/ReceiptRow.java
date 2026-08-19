package com.cibertec.sga.receipt.infrastructure.persistence;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Proyección de solo lectura de {@code "Receipt"} con su tipo resuelto vía JOIN — usada por los
 * reportes de movimientos (RF-32).
 */
public interface ReceiptRow {

    UUID getUuid();

    UUID getReceiptTypeUuid();

    String getReceiptTypeName();

    Long getCorrelativeNumber();

    LocalDate getIssueDate();

    BigDecimal getAmount();

    String getDescription();

    UUID getCurrencyUuid();

    String getCurrencyCode();

    String getCurrencyName();
}
