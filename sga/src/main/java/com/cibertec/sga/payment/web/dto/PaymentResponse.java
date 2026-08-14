package com.cibertec.sga.payment.web.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record PaymentResponse(
    UUID uuid,
    ReceiptRef receipt,
    LocalDate paymentDate,
    BigDecimal totalAmount,
    List<DetailRef> details,
    CreatedByRef createdBy
) {
    public record ReceiptRef(UUID uuid, String receiptTypeName, Long correlativeNumber, LocalDate issueDate, BigDecimal amount) {
    }

    public record DetailRef(UUID accountReceivableUuid, BigDecimal amount) {
    }

    public record CreatedByRef(UUID uuid, String username) {
    }
}
