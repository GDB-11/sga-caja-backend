package com.cibertec.sga.common.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

/**
 * Traduce denegaciones de autorización (filtro {@code .anyRequest().authenticated()} y
 * {@code @PreAuthorize} de método) a la forma estándar {@link ErrorResponse} con 403 — solo
 * llega aquí una request ya autenticada (RNF-02); sin autenticación va a
 * {@link RestAuthenticationEntryPoint}.
 */
@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public RestAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(
        HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException
    ) throws java.io.IOException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), ErrorResponse.of(
            HttpStatus.FORBIDDEN.value(),
            HttpStatus.FORBIDDEN.getReasonPhrase(),
            "No tiene permisos para realizar esta acción",
            request.getRequestURI()
        ));
    }
}
