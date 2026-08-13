package com.cibertec.sga.payment.application;

import com.cibertec.sga.common.result.Result;
import com.cibertec.sga.payment.domain.error.PaymentError;
import com.cibertec.sga.payment.domain.model.Payment;
import java.util.List;
import java.util.UUID;

/**
 * Casos de uso de {@code Payment} (RF-22, RF-23): calcular el total de las cuentas por cobrar
 * seleccionadas y procesar su pago, emitiendo el recibo correspondiente. Es la única interfaz
 * que se inyecta en {@code PaymentController}.
 */
public interface IPaymentService {

    Result<PaymentTotal, PaymentError> computeTotal(List<UUID> accountReceivableUuids);

    Result<Payment, PaymentError> processPayment(List<UUID> accountReceivableUuids);

    Result<Payment, PaymentError> findByUuid(UUID uuid);
}
