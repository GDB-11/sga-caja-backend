package com.cibertec.sga.member.web.dto;

import java.time.LocalDate;
import java.util.UUID;

public record MemberResponse(
    UUID uuid,
    String code,
    String firstName,
    String lastName,
    String shareNumber,
    StageRef stage,
    LocalDate birthDate,
    boolean active
) {
    public record StageRef(UUID uuid, String name) {
    }
}
