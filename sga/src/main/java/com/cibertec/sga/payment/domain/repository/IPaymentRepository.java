package com.cibertec.sga.payment.domain.repository;

import com.cibertec.sga.currency.domain.model.Currency;
import com.cibertec.sga.payment.domain.model.Payment;
import com.cibertec.sga.payment.domain.model.PaymentDetailRef;
import com.cibertec.sga.receipt.domain.model.Receipt;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de persistencia para {@link Payment}, implementado en {@code infrastructure}.
 */
public interface IPaymentRepository {

    /**
     * Crea el {@code Payment} y sus {@code PaymentDetail} para un {@link Receipt} ya emitido
     * (RF-23). El {@code Receipt} se crea antes, vía {@code IReceiptRepository}, en la misma
     * transacción de {@code PaymentService}.
     */
    Payment create(Receipt receipt, BigDecimal totalAmount, Currency currency, List<PaymentDetailRef> details);

    Optional<Payment> findByUuid(UUID uuid);
}
