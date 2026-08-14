package com.cibertec.sga.expense.infrastructure.persistence;

import com.cibertec.sga.expense.domain.model.CreatedByRef;
import com.cibertec.sga.expense.domain.model.Expense;
import com.cibertec.sga.expense.domain.model.ExpenseBulkUploadRef;
import com.cibertec.sga.expensereason.domain.model.ExpenseReason;
import com.cibertec.sga.expensereason.infrastructure.persistence.ExpenseReasonJpaRepository;
import com.cibertec.sga.expensestatus.domain.model.ExpenseStatus;
import com.cibertec.sga.expensestatus.infrastructure.persistence.ExpenseStatusJpaRepository;
import com.cibertec.sga.provider.domain.model.Provider;
import com.cibertec.sga.provider.infrastructure.persistence.ProviderJpaRepository;
import com.cibertec.sga.receipt.domain.model.Receipt;
import com.cibertec.sga.receipt.infrastructure.persistence.ReceiptJpaRepository;
import com.cibertec.sga.receipttype.domain.model.ReceiptType;
import org.springframework.stereotype.Component;

/**
 * Traduce entre {@link ExpenseEntity}/{@link ExpenseRow} (persistencia) y {@link Expense}
 * (modelo de dominio). Resuelve {@code ProviderId}/{@code ExpenseReasonId}/{@code
 * ExpenseStatusId}/{@code ExpenseBulkUploadId} a partir de los {@code Uuid} del modelo de
 * dominio vía los {@code JpaRepository} de esos módulos (dependencia infra-a-infra) — el modelo
 * de dominio nunca conoce Ids internos.
 */
@Component
public class ExpenseMapper {

    private final ProviderJpaRepository providerJpaRepository;
    private final ExpenseReasonJpaRepository expenseReasonJpaRepository;
    private final ExpenseStatusJpaRepository expenseStatusJpaRepository;
    private final ExpenseBulkUploadJpaRepository expenseBulkUploadJpaRepository;
    private final ReceiptJpaRepository receiptJpaRepository;

    public ExpenseMapper(
        ProviderJpaRepository providerJpaRepository,
        ExpenseReasonJpaRepository expenseReasonJpaRepository,
        ExpenseStatusJpaRepository expenseStatusJpaRepository,
        ExpenseBulkUploadJpaRepository expenseBulkUploadJpaRepository,
        ReceiptJpaRepository receiptJpaRepository
    ) {
        this.providerJpaRepository = providerJpaRepository;
        this.expenseReasonJpaRepository = expenseReasonJpaRepository;
        this.expenseStatusJpaRepository = expenseStatusJpaRepository;
        this.expenseBulkUploadJpaRepository = expenseBulkUploadJpaRepository;
        this.receiptJpaRepository = receiptJpaRepository;
    }

    public ExpenseEntity toNewEntity(Expense expense) {
        return ExpenseEntity.builder()
            .documentNumber(expense.getDocumentNumber())
            .providerId(providerJpaRepository.findByUuid(expense.getProvider().getUuid()).orElseThrow().getId())
            .expenseDate(expense.getExpenseDate())
            .amount(expense.getAmount())
            .associatedDocument(expense.getAssociatedDocument())
            .expenseReasonId(expenseReasonJpaRepository.findByUuid(expense.getExpenseReason().getUuid()).orElseThrow().getId())
            .expenseStatusId(expenseStatusJpaRepository.findByUuid(expense.getStatus().getUuid()).orElseThrow().getId())
            .expenseBulkUploadId(resolveBulkUploadId(expense))
            .build();
    }

    private Long resolveBulkUploadId(Expense expense) {
        if (expense.getBulkUpload() == null) {
            return null;
        }
        return expenseBulkUploadJpaRepository.findEntityByUuid(expense.getBulkUpload().uuid()).orElseThrow().getId();
    }

    public Long resolveStatusId(ExpenseStatus status) {
        return expenseStatusJpaRepository.findByUuid(status.getUuid()).orElseThrow().getId();
    }

    public Long resolveReceiptId(Receipt receipt) {
        return receiptJpaRepository.findEntityByUuid(receipt.getUuid()).orElseThrow().getId();
    }

    public Expense toDomain(ExpenseRow row) {
        Provider provider = Provider.builder()
            .uuid(row.getProviderUuid())
            .name(row.getProviderName())
            .document(row.getProviderDocument())
            .active(Boolean.TRUE.equals(row.getProviderIsActive()))
            .build();

        ExpenseReason expenseReason = ExpenseReason.builder()
            .uuid(row.getExpenseReasonUuid())
            .name(row.getExpenseReasonName())
            .build();

        ExpenseStatus status = ExpenseStatus.builder()
            .uuid(row.getStatusUuid())
            .name(row.getStatusName())
            .build();

        Receipt receipt = row.getReceiptUuid() == null ? null : Receipt.builder()
            .uuid(row.getReceiptUuid())
            .receiptType(ReceiptType.builder().uuid(row.getReceiptTypeUuid()).name(row.getReceiptTypeName()).build())
            .correlativeNumber(row.getReceiptCorrelativeNumber())
            .issueDate(row.getReceiptIssueDate())
            .amount(row.getAmount())
            .build();

        ExpenseBulkUploadRef bulkUpload = row.getBulkUploadUuid() == null
            ? null
            : new ExpenseBulkUploadRef(row.getBulkUploadUuid(), row.getBulkUploadFileName());

        return Expense.builder()
            .uuid(row.getUuid())
            .documentNumber(row.getDocumentNumber())
            .provider(provider)
            .expenseDate(row.getExpenseDate())
            .amount(row.getAmount())
            .associatedDocument(row.getAssociatedDocument())
            .expenseReason(expenseReason)
            .status(status)
            .receipt(receipt)
            .bulkUpload(bulkUpload)
            .createdBy(new CreatedByRef(row.getCreatedByUuid(), row.getCreatedByUsername()))
            .build();
    }
}
