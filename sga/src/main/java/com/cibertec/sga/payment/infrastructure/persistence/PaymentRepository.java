package com.cibertec.sga.payment.infrastructure.persistence;

import com.cibertec.sga.payment.domain.model.Payment;
import com.cibertec.sga.payment.domain.model.PaymentDetailRef;
import com.cibertec.sga.payment.domain.repository.IPaymentRepository;
import com.cibertec.sga.receipt.domain.model.Receipt;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentRepository implements IPaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;
    private final PaymentDetailJpaRepository paymentDetailJpaRepository;
    private final PaymentMapper mapper;

    public PaymentRepository(
        PaymentJpaRepository paymentJpaRepository, PaymentDetailJpaRepository paymentDetailJpaRepository, PaymentMapper mapper
    ) {
        this.paymentJpaRepository = paymentJpaRepository;
        this.paymentDetailJpaRepository = paymentDetailJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Payment create(Receipt receipt, BigDecimal totalAmount, List<PaymentDetailRef> details) {
        PaymentEntity savedPayment = paymentJpaRepository.save(mapper.toNewEntity(receipt, totalAmount));

        List<PaymentDetailEntity> detailEntities =
            details.stream().map(detail -> mapper.toNewDetailEntity(savedPayment.getId(), detail)).toList();
        paymentDetailJpaRepository.saveAll(detailEntities);

        return findByUuid(savedPayment.getUuid()).orElseThrow();
    }

    @Override
    public Optional<Payment> findByUuid(UUID uuid) {
        return paymentJpaRepository.findRowByUuid(uuid).map(row -> {
            Long paymentId = paymentJpaRepository.findEntityByUuid(uuid).orElseThrow().getId();
            List<PaymentDetailRef> details = paymentDetailJpaRepository.findRowsByPaymentId(paymentId).stream()
                .map(detailRow -> new PaymentDetailRef(detailRow.getAccountReceivableUuid(), detailRow.getAmount()))
                .toList();
            return mapper.toDomain(row, details);
        });
    }
}
