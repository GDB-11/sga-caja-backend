package com.cibertec.sga.auth.web.dto;

public record AccessTokenResponse(
    String accessToken,
    String tokenType,
    long expiresIn,
    UserProfileResponse user
) {
}
