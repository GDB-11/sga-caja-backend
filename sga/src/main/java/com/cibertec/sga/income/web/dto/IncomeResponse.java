package com.cibertec.sga.income.web.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record IncomeResponse(
    UUID uuid,
    ReceiptRef receipt,
    String depositorName,
    IncomeCategoryRef incomeCategory,
    CurrencyRef currency,
    String concept,
    BigDecimal amount
) {
    public record ReceiptRef(UUID uuid, String receiptTypeName, Long correlativeNumber, LocalDate issueDate) {
    }

    public record IncomeCategoryRef(UUID uuid, String name) {
    }

    public record CurrencyRef(UUID uuid, String code, String name) {
    }
}
