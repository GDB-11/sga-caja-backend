package com.cibertec.sga.accountreceivable.domain.model;

import java.time.LocalDate;

/**
 * Cuenta por cobrar junto con el movimiento que la liquidó, si corresponde — pago de caja
 * (RF-23) o canje bancario (RF-24). Usado por el resumen de socio/puesto (RF-26).
 * {@code settlementMethod}/{@code settledDate}/{@code receiptCorrelative} quedan {@code null}
 * mientras la cuenta esté pendiente o exonerada (no liquidada mediante un movimiento).
 */
public record AccountReceivableMovement(
    AccountReceivable accountReceivable,
    String settlementMethod,
    LocalDate settledDate,
    Long receiptCorrelative
) {
}
