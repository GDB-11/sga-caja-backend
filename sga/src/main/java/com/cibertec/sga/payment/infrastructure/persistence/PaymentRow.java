package com.cibertec.sga.payment.infrastructure.persistence;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Proyección de solo lectura de {@code "Payment"} con su {@code Receipt} resuelto vía JOIN,
 * para evitar N+1 al obtener un pago.
 */
public interface PaymentRow {

    UUID getUuid();

    LocalDate getPaymentDate();

    BigDecimal getTotalAmount();

    UUID getReceiptUuid();

    Long getReceiptCorrelativeNumber();

    LocalDate getReceiptIssueDate();

    BigDecimal getReceiptAmount();

    String getReceiptDescription();

    UUID getReceiptTypeUuid();

    String getReceiptTypeName();

    UUID getCreatedByUuid();

    String getCreatedByUsername();

    UUID getCurrencyUuid();

    String getCurrencyCode();

    String getCurrencyName();
}
