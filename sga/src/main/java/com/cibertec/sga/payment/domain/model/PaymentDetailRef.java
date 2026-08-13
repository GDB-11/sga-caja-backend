package com.cibertec.sga.payment.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Referencia mínima a una cuenta por cobrar incluida en un pago (RF-21–RF-23) — evita que
 * {@link Payment} dependa del modelo de dominio completo de {@code AccountReceivable}.
 */
public record PaymentDetailRef(UUID accountReceivableUuid, BigDecimal amount) {
}
