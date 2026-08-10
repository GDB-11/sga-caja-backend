package com.cibertec.sga.common.result;

/**
 * Contrato que implementa cada error de negocio esperado (no encontrado, duplicado,
 * regla de negocio violada), definido como {@code sealed interface} por módulo.
 */
public interface DomainError {
    String code();
    String message();
    ErrorType type();
}
