package com.cibertec.sga.common.result;

import com.cibertec.sga.common.web.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Utilidad estática (no se inyecta) que traduce un {@link Result} a {@link ResponseEntity},
 * mapeando {@link ErrorType} al {@link HttpStatus} correspondiente.
 */
public final class ResultResponse {

    private ResultResponse() {
    }

    public static <T, E extends DomainError> ResponseEntity<?> ok(Result<T, E> result, HttpServletRequest request) {
        return toResponseEntity(result, HttpStatus.OK, request);
    }

    public static <T, E extends DomainError> ResponseEntity<?> created(Result<T, E> result, HttpServletRequest request) {
        return toResponseEntity(result, HttpStatus.CREATED, request);
    }

    public static <T, E extends DomainError> ResponseEntity<?> toResponseEntity(
        Result<T, E> result, HttpStatus successStatus, HttpServletRequest request
    ) {
        if (result.isSuccess()) {
            return ResponseEntity.status(successStatus).body(result.getValue());
        }

        DomainError error = result.getError();
        HttpStatus status = toHttpStatus(error.type());
        return ResponseEntity.status(status).body(
            ErrorResponse.of(status.value(), error.code(), error.message(), request.getRequestURI())
        );
    }

    private static HttpStatus toHttpStatus(ErrorType type) {
        return switch (type) {
            case VALIDATION -> HttpStatus.BAD_REQUEST;
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            case CONFLICT -> HttpStatus.CONFLICT;
            case UNAUTHORIZED -> HttpStatus.UNAUTHORIZED;
            case FORBIDDEN -> HttpStatus.FORBIDDEN;
            case INTERNAL -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
