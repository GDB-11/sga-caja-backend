package com.cibertec.sga.expense.application;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record RegisterExpenseCommand(
    String documentNumber,
    UUID providerUuid,
    LocalDate expenseDate,
    BigDecimal amount,
    String associatedDocument,
    UUID expenseReasonUuid
) {
}
