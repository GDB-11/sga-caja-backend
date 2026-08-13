package com.cibertec.sga.payment.application;

import com.cibertec.sga.accountreceivable.domain.model.AccountReceivable;
import com.cibertec.sga.accountreceivable.domain.repository.IAccountReceivableRepository;
import com.cibertec.sga.accountreceivablestatus.domain.model.AccountReceivableStatus;
import com.cibertec.sga.accountreceivablestatus.domain.repository.IAccountReceivableStatusRepository;
import com.cibertec.sga.common.result.Result;
import com.cibertec.sga.payment.domain.error.PaymentError;
import com.cibertec.sga.payment.domain.model.Payment;
import com.cibertec.sga.payment.domain.model.PaymentDetailRef;
import com.cibertec.sga.payment.domain.repository.IPaymentRepository;
import com.cibertec.sga.receipt.domain.model.Receipt;
import com.cibertec.sga.receipt.domain.repository.IReceiptRepository;
import com.cibertec.sga.receipttype.domain.model.ReceiptType;
import com.cibertec.sga.receipttype.domain.repository.IReceiptTypeRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService implements IPaymentService {

    private static final String STATUS_PENDING = "Pending";
    private static final String STATUS_PAID = "Paid";
    private static final String RECEIPT_TYPE_INCOME = "Income";

    private final IPaymentRepository paymentRepository;
    private final IAccountReceivableRepository accountReceivableRepository;
    private final IAccountReceivableStatusRepository accountReceivableStatusRepository;
    private final IReceiptRepository receiptRepository;
    private final IReceiptTypeRepository receiptTypeRepository;

    public PaymentService(
        IPaymentRepository paymentRepository,
        IAccountReceivableRepository accountReceivableRepository,
        IAccountReceivableStatusRepository accountReceivableStatusRepository,
        IReceiptRepository receiptRepository,
        IReceiptTypeRepository receiptTypeRepository
    ) {
        this.paymentRepository = paymentRepository;
        this.accountReceivableRepository = accountReceivableRepository;
        this.accountReceivableStatusRepository = accountReceivableStatusRepository;
        this.receiptRepository = receiptRepository;
        this.receiptTypeRepository = receiptTypeRepository;
    }

    @Override
    public Result<PaymentTotal, PaymentError> computeTotal(List<UUID> accountReceivableUuids) {
        Result<List<AccountReceivable>, PaymentError> selection = validateSelection(accountReceivableUuids);
        if (selection.isFailure()) {
            return Result.failure(selection.getError());
        }
        List<AccountReceivable> accountReceivables = selection.getValue();
        BigDecimal total = accountReceivables.stream().map(AccountReceivable::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        return Result.success(new PaymentTotal(accountReceivables, total));
    }

    @Override
    @Transactional
    public Result<Payment, PaymentError> processPayment(List<UUID> accountReceivableUuids) {
        Result<List<AccountReceivable>, PaymentError> selection = validateSelection(accountReceivableUuids);
        if (selection.isFailure()) {
            return Result.failure(selection.getError());
        }
        List<AccountReceivable> accountReceivables = selection.getValue();
        BigDecimal total = accountReceivables.stream().map(AccountReceivable::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        ReceiptType incomeType = receiptTypeRepository.findByName(RECEIPT_TYPE_INCOME).orElseThrow();
        Receipt receipt = receiptRepository.insert(Receipt.builder().receiptType(incomeType).amount(total).build());

        List<PaymentDetailRef> details = accountReceivables.stream()
            .map(ar -> new PaymentDetailRef(ar.getUuid(), ar.getAmount()))
            .toList();
        Payment payment = paymentRepository.create(receipt, total, details);

        AccountReceivableStatus paidStatus = accountReceivableStatusRepository.findByName(STATUS_PAID).orElseThrow();
        for (AccountReceivable accountReceivable : accountReceivables) {
            accountReceivableRepository.updateStatus(accountReceivable.getUuid(), paidStatus);
        }

        return Result.success(payment);
    }

    @Override
    public Result<Payment, PaymentError> findByUuid(UUID uuid) {
        return paymentRepository.findByUuid(uuid)
            .map(Result::<Payment, PaymentError>success)
            .orElseGet(() -> Result.failure(new PaymentError.NotFound(uuid.toString())));
    }

    private Result<List<AccountReceivable>, PaymentError> validateSelection(List<UUID> accountReceivableUuids) {
        if (accountReceivableUuids == null || accountReceivableUuids.isEmpty()) {
            return Result.failure(new PaymentError.EmptySelection());
        }

        List<AccountReceivable> accountReceivables = new ArrayList<>();
        for (UUID uuid : accountReceivableUuids) {
            var accountReceivableOpt = accountReceivableRepository.findByUuid(uuid);
            if (accountReceivableOpt.isEmpty()) {
                return Result.failure(new PaymentError.AccountReceivableNotFound(uuid.toString()));
            }
            AccountReceivable accountReceivable = accountReceivableOpt.get();
            if (!accountReceivable.getStatus().getName().equals(STATUS_PENDING)) {
                return Result.failure(new PaymentError.AccountReceivableNotPending(uuid.toString()));
            }
            accountReceivables.add(accountReceivable);
        }
        return Result.success(accountReceivables);
    }
}
