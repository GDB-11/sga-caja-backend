package com.cibertec.sga.expense.web.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ExpenseResponse(
    UUID uuid,
    String documentNumber,
    ProviderRef provider,
    LocalDate expenseDate,
    BigDecimal amount,
    String associatedDocument,
    ExpenseReasonRef expenseReason,
    StatusRef status,
    ReceiptRef receipt,
    BulkUploadRef bulkUpload,
    CreatedByRef createdBy,
    CurrencyRef currency
) {
    public record ProviderRef(UUID uuid, String name) {
    }

    public record ExpenseReasonRef(UUID uuid, String name) {
    }

    public record StatusRef(UUID uuid, String name) {
    }

    public record ReceiptRef(UUID uuid, String receiptTypeName, Long correlativeNumber, LocalDate issueDate) {
    }

    public record BulkUploadRef(UUID uuid, String fileName) {
    }

    public record CreatedByRef(UUID uuid, String username) {
    }

    public record CurrencyRef(UUID uuid, String code, String name) {
    }
}
