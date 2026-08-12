package com.cibertec.sga.auth.web;

import com.cibertec.sga.auth.application.AuthSession;
import com.cibertec.sga.auth.application.IAuthService;
import com.cibertec.sga.auth.domain.error.AuthError;
import com.cibertec.sga.auth.web.dto.LoginRequest;
import com.cibertec.sga.common.result.Result;
import com.cibertec.sga.common.result.ResultResponse;
import com.cibertec.sga.common.security.AuthenticatedUser;
import com.cibertec.sga.user.domain.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints de sesión (RF-01-RF-04): login, refresco de sesión, cierre de sesión y perfil del
 * usuario autenticado.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Sesión", description = "Login, refresco de sesión, logout y perfil del usuario autenticado")
public class AuthController {

    private static final String REFRESH_TOKEN_COOKIE = "refreshToken";
    private static final String REFRESH_TOKEN_COOKIE_PATH = "/api/auth";

    private final IAuthService authService;
    private final AuthDtoMapper dtoMapper;

    public AuthController(IAuthService authService, AuthDtoMapper dtoMapper) {
        this.authService = authService;
        this.dtoMapper = dtoMapper;
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión con usuario y contraseña")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        Result<AuthSession, AuthError> result = authService.login(request.username(), request.password());
        return toSessionResponse(result, httpRequest);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renovar el access token usando el refresh token de la cookie")
    public ResponseEntity<?> refresh(
        @CookieValue(name = REFRESH_TOKEN_COOKIE, required = false) String refreshToken, HttpServletRequest httpRequest
    ) {
        Result<AuthSession, AuthError> result = authService.refresh(refreshToken);
        return toSessionResponse(result, httpRequest);
    }

    @PostMapping("/logout")
    @Operation(summary = "Cerrar sesión y revocar el refresh token")
    public ResponseEntity<?> logout(
        @CookieValue(name = REFRESH_TOKEN_COOKIE, required = false) String refreshToken
    ) {
        authService.logout(refreshToken);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
            .header(HttpHeaders.SET_COOKIE, clearRefreshTokenCookie().toString())
            .build();
    }

    @GetMapping("/me")
    @Operation(summary = "Obtener la identidad del usuario autenticado")
    public ResponseEntity<?> me(@AuthenticationPrincipal AuthenticatedUser principal, HttpServletRequest httpRequest) {
        Result<User, AuthError> result = authService.me(principal.uuid());
        return ResultResponse.ok(result.map(dtoMapper::toProfileResponse), httpRequest);
    }

    private ResponseEntity<?> toSessionResponse(Result<AuthSession, AuthError> result, HttpServletRequest httpRequest) {
        if (result.isFailure()) {
            return ResultResponse.ok(result, httpRequest);
        }

        AuthSession session = result.getValue();
        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE, session.rawRefreshToken())
            .httpOnly(true)
            .secure(true)
            .sameSite("Lax")
            .path(REFRESH_TOKEN_COOKIE_PATH)
            .maxAge(session.refreshTokenExpiresInSeconds())
            .build();

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(dtoMapper.toAccessTokenResponse(session));
    }

    private ResponseCookie clearRefreshTokenCookie() {
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE, "")
            .httpOnly(true)
            .secure(true)
            .sameSite("Lax")
            .path(REFRESH_TOKEN_COOKIE_PATH)
            .maxAge(0)
            .build();
    }
}
