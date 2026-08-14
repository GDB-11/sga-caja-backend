package com.cibertec.sga.payment.infrastructure.persistence;

import com.cibertec.sga.accountreceivable.infrastructure.persistence.AccountReceivableJpaRepository;
import com.cibertec.sga.payment.domain.model.CreatedByRef;
import com.cibertec.sga.payment.domain.model.Payment;
import com.cibertec.sga.payment.domain.model.PaymentDetailRef;
import com.cibertec.sga.receipt.domain.model.Receipt;
import com.cibertec.sga.receipt.infrastructure.persistence.ReceiptJpaRepository;
import com.cibertec.sga.receipttype.domain.model.ReceiptType;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Traduce entre {@link PaymentEntity}/{@link PaymentDetailEntity} (persistencia) y
 * {@link Payment} (modelo de dominio). Resuelve {@code ReceiptId}/{@code AccountReceivableId} a
 * partir de los {@code Uuid} del modelo de dominio vía los {@code JpaRepository} de esos
 * módulos (dependencia infra-a-infra) — el modelo de dominio nunca conoce Ids internos.
 */
@Component
public class PaymentMapper {

    private final ReceiptJpaRepository receiptJpaRepository;
    private final AccountReceivableJpaRepository accountReceivableJpaRepository;

    public PaymentMapper(ReceiptJpaRepository receiptJpaRepository, AccountReceivableJpaRepository accountReceivableJpaRepository) {
        this.receiptJpaRepository = receiptJpaRepository;
        this.accountReceivableJpaRepository = accountReceivableJpaRepository;
    }

    public PaymentEntity toNewEntity(Receipt receipt, BigDecimal totalAmount) {
        Long receiptId = receiptJpaRepository.findEntityByUuid(receipt.getUuid()).orElseThrow().getId();
        return PaymentEntity.builder().receiptId(receiptId).totalAmount(totalAmount).build();
    }

    public PaymentDetailEntity toNewDetailEntity(Long paymentId, PaymentDetailRef detail) {
        Long accountReceivableId =
            accountReceivableJpaRepository.findEntityByUuid(detail.accountReceivableUuid()).orElseThrow().getId();
        return PaymentDetailEntity.builder()
            .paymentId(paymentId)
            .accountReceivableId(accountReceivableId)
            .amount(detail.amount())
            .build();
    }

    public Payment toDomain(PaymentRow row, List<PaymentDetailRef> details) {
        ReceiptType receiptType = ReceiptType.builder().uuid(row.getReceiptTypeUuid()).name(row.getReceiptTypeName()).build();
        Receipt receipt = Receipt.builder()
            .uuid(row.getReceiptUuid())
            .receiptType(receiptType)
            .correlativeNumber(row.getReceiptCorrelativeNumber())
            .issueDate(row.getReceiptIssueDate())
            .amount(row.getReceiptAmount())
            .description(row.getReceiptDescription())
            .build();
        return Payment.builder()
            .uuid(row.getUuid())
            .receipt(receipt)
            .paymentDate(row.getPaymentDate())
            .totalAmount(row.getTotalAmount())
            .details(details)
            .createdBy(new CreatedByRef(row.getCreatedByUuid(), row.getCreatedByUsername()))
            .build();
    }
}
