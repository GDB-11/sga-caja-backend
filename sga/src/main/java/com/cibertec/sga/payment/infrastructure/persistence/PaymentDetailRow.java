package com.cibertec.sga.payment.infrastructure.persistence;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Proyección de solo lectura de {@code "PaymentDetail"} con la cuenta por cobrar resuelta vía
 * JOIN.
 */
public interface PaymentDetailRow {

    UUID getAccountReceivableUuid();

    BigDecimal getAmount();
}
