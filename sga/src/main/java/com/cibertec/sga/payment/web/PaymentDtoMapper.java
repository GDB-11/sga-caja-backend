package com.cibertec.sga.payment.web;

import com.cibertec.sga.accountreceivable.domain.model.AccountReceivable;
import com.cibertec.sga.payment.application.PaymentTotal;
import com.cibertec.sga.payment.domain.model.Payment;
import com.cibertec.sga.payment.domain.model.PaymentDetailRef;
import com.cibertec.sga.payment.web.dto.PaymentResponse;
import com.cibertec.sga.payment.web.dto.PaymentTotalResponse;
import org.springframework.stereotype.Component;

/**
 * Traduce entre los modelos de dominio {@link Payment}/{@link PaymentTotal} y los DTOs de
 * {@code web}.
 */
@Component
public class PaymentDtoMapper {

    public PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
            payment.getUuid(),
            new PaymentResponse.ReceiptRef(
                payment.getReceipt().getUuid(), payment.getReceipt().getReceiptType().getName(),
                payment.getReceipt().getCorrelativeNumber(), payment.getReceipt().getIssueDate(), payment.getReceipt().getAmount()
            ),
            payment.getPaymentDate(),
            payment.getTotalAmount(),
            payment.getDetails().stream().map(this::toResponse).toList(),
            new PaymentResponse.CreatedByRef(payment.getCreatedBy().uuid(), payment.getCreatedBy().username())
        );
    }

    private PaymentResponse.DetailRef toResponse(PaymentDetailRef detail) {
        return new PaymentResponse.DetailRef(detail.accountReceivableUuid(), detail.amount());
    }

    public PaymentTotalResponse toResponse(PaymentTotal paymentTotal) {
        return new PaymentTotalResponse(
            paymentTotal.accountReceivables().stream().map(this::toItem).toList(), paymentTotal.total()
        );
    }

    private PaymentTotalResponse.Item toItem(AccountReceivable accountReceivable) {
        return new PaymentTotalResponse.Item(accountReceivable.getUuid(), accountReceivable.getAmount());
    }
}
