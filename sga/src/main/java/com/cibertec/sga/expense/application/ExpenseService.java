package com.cibertec.sga.expense.application;

import com.cibertec.sga.common.result.Result;
import com.cibertec.sga.expense.domain.error.ExpenseError;
import com.cibertec.sga.expense.domain.model.Expense;
import com.cibertec.sga.expense.domain.model.ExpenseBulkUploadRef;
import com.cibertec.sga.expense.domain.repository.IExpenseBulkUploadRepository;
import com.cibertec.sga.expense.domain.repository.IExpenseRepository;
import com.cibertec.sga.expensereason.domain.model.ExpenseReason;
import com.cibertec.sga.expensereason.domain.repository.IExpenseReasonRepository;
import com.cibertec.sga.expensestatus.domain.model.ExpenseStatus;
import com.cibertec.sga.expensestatus.domain.repository.IExpenseStatusRepository;
import com.cibertec.sga.provider.domain.model.Provider;
import com.cibertec.sga.provider.domain.repository.IProviderRepository;
import com.cibertec.sga.receipt.domain.model.Receipt;
import com.cibertec.sga.receipt.domain.repository.IReceiptRepository;
import com.cibertec.sga.receipttype.domain.model.ReceiptType;
import com.cibertec.sga.receipttype.domain.repository.IReceiptTypeRepository;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExpenseService implements IExpenseService {

    private static final String STATUS_PENDING = "Pending";
    private static final String STATUS_VOIDED = "Voided";
    private static final String STATUS_PROCESSED = "Processed";
    private static final String BULK_UPLOAD_STATUS_PROCESSED = "Processed";
    private static final String BULK_UPLOAD_STATUS_FAILED = "Failed";
    private static final String RECEIPT_TYPE_EXPENSE = "Expense";

    private final IExpenseRepository expenseRepository;
    private final IExpenseBulkUploadRepository expenseBulkUploadRepository;
    private final IProviderRepository providerRepository;
    private final IExpenseReasonRepository expenseReasonRepository;
    private final IExpenseStatusRepository expenseStatusRepository;
    private final IReceiptRepository receiptRepository;
    private final IReceiptTypeRepository receiptTypeRepository;

    public ExpenseService(
        IExpenseRepository expenseRepository,
        IExpenseBulkUploadRepository expenseBulkUploadRepository,
        IProviderRepository providerRepository,
        IExpenseReasonRepository expenseReasonRepository,
        IExpenseStatusRepository expenseStatusRepository,
        IReceiptRepository receiptRepository,
        IReceiptTypeRepository receiptTypeRepository
    ) {
        this.expenseRepository = expenseRepository;
        this.expenseBulkUploadRepository = expenseBulkUploadRepository;
        this.providerRepository = providerRepository;
        this.expenseReasonRepository = expenseReasonRepository;
        this.expenseStatusRepository = expenseStatusRepository;
        this.receiptRepository = receiptRepository;
        this.receiptTypeRepository = receiptTypeRepository;
    }

    @Override
    public Result<Expense, ExpenseError> register(RegisterExpenseCommand command) {
        var providerOpt = providerRepository.findByUuid(command.providerUuid());
        if (providerOpt.isEmpty()) {
            return Result.failure(new ExpenseError.ProviderNotFound(command.providerUuid().toString()));
        }
        Provider provider = providerOpt.get();
        if (!provider.isActive()) {
            return Result.failure(new ExpenseError.ProviderInactive(command.providerUuid().toString()));
        }

        var expenseReasonOpt = expenseReasonRepository.findByUuid(command.expenseReasonUuid());
        if (expenseReasonOpt.isEmpty()) {
            return Result.failure(new ExpenseError.ExpenseReasonNotFound(command.expenseReasonUuid().toString()));
        }

        ExpenseStatus pendingStatus = expenseStatusRepository.findByName(STATUS_PENDING).orElseThrow();

        Expense expense = Expense.builder()
            .documentNumber(command.documentNumber())
            .provider(provider)
            .expenseDate(command.expenseDate())
            .amount(command.amount())
            .associatedDocument(command.associatedDocument())
            .expenseReason(expenseReasonOpt.get())
            .status(pendingStatus)
            .build();

        return Result.success(expenseRepository.insert(expense));
    }

    @Override
    @Transactional
    public Result<List<Expense>, ExpenseError> registerBulk(String fileName, InputStream fileContent) {
        BulkParseResult parseResult;
        try {
            parseResult = ExpenseBulkFileParser.parse(fileContent);
        } catch (IllegalArgumentException e) {
            return Result.failure(new ExpenseError.InvalidBulkFile(e.getMessage()));
        }

        List<String> errors = new ArrayList<>(parseResult.errors());
        List<ValidatedRow> validated = new ArrayList<>();

        for (BulkExpenseRow row : parseResult.rows()) {
            Optional<Provider> providerOpt = findActiveProviderByName(row.providerName());
            if (providerOpt.isEmpty()) {
                errors.add("Fila " + row.rowNumber() + ": proveedor no encontrado o inactivo: " + row.providerName());
                continue;
            }
            Optional<ExpenseReason> expenseReasonOpt = expenseReasonRepository.findByName(row.expenseReasonName());
            if (expenseReasonOpt.isEmpty()) {
                errors.add("Fila " + row.rowNumber() + ": motivo de egreso no encontrado: " + row.expenseReasonName());
                continue;
            }
            validated.add(new ValidatedRow(row, providerOpt.get(), expenseReasonOpt.get()));
        }

        if (!errors.isEmpty()) {
            expenseBulkUploadRepository.create(fileName, BULK_UPLOAD_STATUS_FAILED);
            return Result.failure(new ExpenseError.BulkValidationFailed(errors));
        }

        ExpenseStatus pendingStatus = expenseStatusRepository.findByName(STATUS_PENDING).orElseThrow();
        ExpenseBulkUploadRef bulkUpload = expenseBulkUploadRepository.create(fileName, BULK_UPLOAD_STATUS_PROCESSED);
        List<Expense> expenses = validated.stream()
            .map(validatedRow -> Expense.builder()
                .documentNumber(validatedRow.row().documentNumber())
                .provider(validatedRow.provider())
                .expenseDate(validatedRow.row().expenseDate())
                .amount(validatedRow.row().amount())
                .associatedDocument(validatedRow.row().associatedDocument())
                .expenseReason(validatedRow.expenseReason())
                .status(pendingStatus)
                .bulkUpload(bulkUpload)
                .build())
            .toList();

        return Result.success(expenseRepository.insertAll(expenses));
    }

    private record ValidatedRow(BulkExpenseRow row, Provider provider, ExpenseReason expenseReason) {
    }

    private Optional<Provider> findActiveProviderByName(String name) {
        return providerRepository.findByName(name).filter(Provider::isActive);
    }

    @Override
    public Result<Expense, ExpenseError> findByUuid(UUID uuid) {
        return expenseRepository.findByUuid(uuid)
            .map(Result::<Expense, ExpenseError>success)
            .orElseGet(() -> Result.failure(new ExpenseError.NotFound(uuid.toString())));
    }

    @Override
    public Page<Expense> search(Integer year, Integer month, Pageable pageable) {
        return expenseRepository.search(year, month, pageable);
    }

    @Override
    @Transactional
    public Result<Expense, ExpenseError> voidExpense(UUID uuid) {
        var expenseOpt = expenseRepository.findByUuidForUpdate(uuid);
        if (expenseOpt.isEmpty()) {
            return Result.failure(new ExpenseError.NotFound(uuid.toString()));
        }
        if (!expenseOpt.get().getStatus().getName().equals(STATUS_PENDING)) {
            return Result.failure(new ExpenseError.NotPending(uuid.toString()));
        }

        ExpenseStatus voidedStatus = expenseStatusRepository.findByName(STATUS_VOIDED).orElseThrow();
        return Result.success(expenseRepository.updateStatus(uuid, voidedStatus));
    }

    @Override
    @Transactional
    public Result<Expense, ExpenseError> processExpense(UUID uuid) {
        var expenseOpt = expenseRepository.findByUuidForUpdate(uuid);
        if (expenseOpt.isEmpty()) {
            return Result.failure(new ExpenseError.NotFound(uuid.toString()));
        }
        Expense expense = expenseOpt.get();
        if (!expense.getStatus().getName().equals(STATUS_PENDING)) {
            return Result.failure(new ExpenseError.NotPending(uuid.toString()));
        }

        ReceiptType expenseReceiptType = receiptTypeRepository.findByName(RECEIPT_TYPE_EXPENSE).orElseThrow();
        Receipt receipt = receiptRepository.insert(Receipt.builder().receiptType(expenseReceiptType).amount(expense.getAmount()).build());

        ExpenseStatus processedStatus = expenseStatusRepository.findByName(STATUS_PROCESSED).orElseThrow();
        return Result.success(expenseRepository.markProcessed(uuid, processedStatus, receipt));
    }
}
