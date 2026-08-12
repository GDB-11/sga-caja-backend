package com.cibertec.sga.stall.web.dto;

import java.time.LocalDate;
import java.util.UUID;

public record StallResponse(
    UUID uuid,
    String number,
    BusinessTypeRef businessType,
    MemberRef member,
    String tenantName,
    String tenantDocument,
    LocalDate validityStartDate,
    LocalDate validityEndDate,
    boolean active
) {
    public record BusinessTypeRef(UUID uuid, String name) {
    }

    public record MemberRef(UUID uuid, String fullName) {
    }
}
