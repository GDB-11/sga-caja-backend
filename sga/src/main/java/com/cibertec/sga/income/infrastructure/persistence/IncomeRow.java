package com.cibertec.sga.income.infrastructure.persistence;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Proyección de solo lectura de {@code "Income"} con comprobante y categoría resueltos vía
 * JOIN, para evitar N+1 al listar/obtener ingresos.
 */
public interface IncomeRow {

    UUID getUuid();

    UUID getReceiptUuid();

    Long getReceiptCorrelativeNumber();

    LocalDate getReceiptIssueDate();

    UUID getReceiptTypeUuid();

    String getReceiptTypeName();

    String getDepositorName();

    UUID getIncomeCategoryUuid();

    String getIncomeCategoryName();

    UUID getCurrencyUuid();

    String getCurrencyCode();

    String getCurrencyName();

    String getConcept();

    BigDecimal getAmount();
}
