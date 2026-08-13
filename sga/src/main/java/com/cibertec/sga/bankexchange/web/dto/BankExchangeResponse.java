package com.cibertec.sga.bankexchange.web.dto;

import com.cibertec.sga.accountreceivable.web.dto.AccountReceivableResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record BankExchangeResponse(
    UUID uuid,
    AccountReceivableResponse accountReceivable,
    BankRef bank,
    ReceiptRef receipt,
    LocalDate depositDate,
    BigDecimal amount
) {
    public record BankRef(UUID uuid, String name) {
    }

    public record ReceiptRef(UUID uuid, String receiptTypeName, Long correlativeNumber, LocalDate issueDate) {
    }
}
