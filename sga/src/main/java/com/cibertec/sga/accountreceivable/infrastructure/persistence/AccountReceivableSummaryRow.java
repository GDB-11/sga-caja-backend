package com.cibertec.sga.accountreceivable.infrastructure.persistence;

import java.time.LocalDate;

/**
 * Proyección de {@link AccountReceivableRow} extendida con el movimiento que liquidó la cuenta
 * (pago de caja o canje bancario), si corresponde — usada por el resumen de socio/puesto
 * (RF-26). Todos los campos de movimiento quedan {@code null} para cuentas pendientes o
 * exoneradas.
 */
public interface AccountReceivableSummaryRow extends AccountReceivableRow {

    String getSettlementMethod();

    LocalDate getSettledDate();

    Long getReceiptCorrelative();
}
