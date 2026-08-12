package com.cibertec.sga.common.web;

import jakarta.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Captura excepciones no controladas por la capa {@code application} (errores de negocio
 * esperados se manejan con {@code Result}/{@code ResultResponse}, no aquí) y las traduce
 * a la forma estándar {@link ErrorResponse}.
 *
 * <p>Denegaciones de autorización ({@code AccessDeniedException}/{@code @PreAuthorize}) NO se
 * manejan aquí — se dejan propagar fuera del {@code DispatcherServlet} para que las capture
 * {@code ExceptionTranslationFilter} y las enrute a {@code RestAuthenticationEntryPoint} (401,
 * sin autenticar) o {@code RestAccessDeniedHandler} (403, autenticado sin permiso), que es la
 * única capa con la información de sesión necesaria para distinguir ambos casos correctamente.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
        MethodArgumentNotValidException ex, HttpServletRequest request
    ) {
        String message = ex.getBindingResult().getFieldErrors().stream()
            .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
            .collect(Collectors.joining("; "));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                message,
                request.getRequestURI()
            )
        );
    }

    /**
     * Sin este método, {@code @ExceptionHandler(Exception.class)} de abajo la atraparía primero
     * (es una {@code Exception} más) y nunca llegaría a {@code ExceptionTranslationFilter} —
     * relanzarla es lo que permite que la capture la capa de seguridad.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public void rethrowAccessDenied(AccessDeniedException ex) throws AccessDeniedException {
        throw ex;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            ErrorResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "Error interno del servidor",
                request.getRequestURI()
            )
        );
    }
}
