package com.cibertec.sga.auth.web.dto;

import java.util.UUID;

public record UserProfileResponse(
    UUID uuid,
    String username,
    String firstName,
    String lastName,
    String roleName
) {
}
