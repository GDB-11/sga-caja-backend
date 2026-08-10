package com.cibertec.sga.common.result;

/**
 * Categoría de un {@link DomainError}, usada para traducirlo al código HTTP correspondiente.
 */
public enum ErrorType {
    VALIDATION,
    NOT_FOUND,
    CONFLICT,
    UNAUTHORIZED,
    FORBIDDEN,
    INTERNAL
}
