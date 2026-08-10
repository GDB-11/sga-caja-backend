package com.cibertec.sga.common.web;

import java.time.Instant;

/**
 * Forma estándar de las respuestas de error de la API, tanto para errores de negocio
 * ({@code DomainError} vía {@code ResultResponse}) como para excepciones no controladas
 * (vía {@link GlobalExceptionHandler}).
 */
public record ErrorResponse(
    Instant timestamp,
    int status,
    String error,
    String message,
    String path
) {
    public static ErrorResponse of(int status, String error, String message, String path) {
        return new ErrorResponse(Instant.now(), status, error, message, path);
    }
}
