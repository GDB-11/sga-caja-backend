package com.cibertec.sga.accountreceivable.web.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record AccountReceivableResponse(
    UUID uuid,
    ServiceRef service,
    MemberRef member,
    StallRef stall,
    LocalDate periodStartDate,
    LocalDate periodEndDate,
    BigDecimal amount,
    StatusRef status
) {
    public record ServiceRef(UUID uuid, String name, boolean consumptionBased) {
    }

    public record MemberRef(UUID uuid, String fullName) {
    }

    public record StallRef(UUID uuid, String number) {
    }

    public record StatusRef(UUID uuid, String name) {
    }
}
