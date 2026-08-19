package com.cibertec.sga.payment.application;

import com.cibertec.sga.accountreceivable.domain.model.AccountReceivable;
import com.cibertec.sga.currency.domain.model.Currency;
import java.math.BigDecimal;
import java.util.List;

/**
 * Vista previa del total a pagar por un conjunto de cuentas por cobrar seleccionadas (RF-22),
 * antes de confirmar el pago (RF-23).
 */
public record PaymentTotal(List<AccountReceivable> accountReceivables, BigDecimal total, Currency currency) {
}
