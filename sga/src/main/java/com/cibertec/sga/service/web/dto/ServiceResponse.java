package com.cibertec.sga.service.web.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ServiceResponse(
    UUID uuid,
    String name,
    RecurrenceTypeRef recurrenceType,
    ChargeTargetTypeRef chargeTargetType,
    CurrencyRef currency,
    boolean consumptionBased,
    BigDecimal cost,
    BigDecimal unitCost,
    boolean active
) {
    public record RecurrenceTypeRef(UUID uuid, String name) {
    }

    public record ChargeTargetTypeRef(UUID uuid, String name) {
    }

    public record CurrencyRef(UUID uuid, String code, String name) {
    }
}
