package com.cibertec.sga.accountreceivable.web.dto;

import java.time.LocalDate;

/**
 * Cuenta por cobrar junto con el movimiento que la liquidó (pago o canje), si corresponde —
 * usado por el resumen de socio/puesto (RF-26).
 */
public record AccountReceivableMovementResponse(
    AccountReceivableResponse accountReceivable,
    String settlementMethod,
    LocalDate settledDate,
    Long receiptCorrelative
) {
}
