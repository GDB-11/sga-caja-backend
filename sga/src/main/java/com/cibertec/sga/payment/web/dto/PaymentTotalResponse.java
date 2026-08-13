package com.cibertec.sga.payment.web.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record PaymentTotalResponse(List<Item> items, BigDecimal total) {
    public record Item(UUID accountReceivableUuid, BigDecimal amount) {
    }
}
