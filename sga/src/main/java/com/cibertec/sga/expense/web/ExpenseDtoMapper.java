package com.cibertec.sga.expense.web;

import com.cibertec.sga.expense.application.RegisterExpenseCommand;
import com.cibertec.sga.expense.domain.model.Expense;
import com.cibertec.sga.expense.web.dto.ExpenseResponse;
import com.cibertec.sga.expense.web.dto.RegisterExpenseRequest;
import org.springframework.stereotype.Component;

/**
 * Traduce entre el modelo de dominio {@link Expense} y los DTOs de {@code web}.
 */
@Component
public class ExpenseDtoMapper {

    public ExpenseResponse toResponse(Expense expense) {
        return new ExpenseResponse(
            expense.getUuid(),
            expense.getDocumentNumber(),
            new ExpenseResponse.ProviderRef(expense.getProvider().getUuid(), expense.getProvider().getName()),
            expense.getExpenseDate(),
            expense.getAmount(),
            expense.getAssociatedDocument(),
            new ExpenseResponse.ExpenseReasonRef(expense.getExpenseReason().getUuid(), expense.getExpenseReason().getName()),
            new ExpenseResponse.StatusRef(expense.getStatus().getUuid(), expense.getStatus().getName()),
            expense.getReceipt() == null ? null : new ExpenseResponse.ReceiptRef(
                expense.getReceipt().getUuid(), expense.getReceipt().getReceiptType().getName(),
                expense.getReceipt().getCorrelativeNumber(), expense.getReceipt().getIssueDate()
            ),
            expense.getBulkUpload() == null ? null : new ExpenseResponse.BulkUploadRef(
                expense.getBulkUpload().uuid(), expense.getBulkUpload().fileName()
            )
        );
    }

    public RegisterExpenseCommand toCommand(RegisterExpenseRequest request) {
        return new RegisterExpenseCommand(
            request.documentNumber(), request.providerUuid(), request.expenseDate(), request.amount(),
            request.associatedDocument(), request.expenseReasonUuid()
        );
    }
}
