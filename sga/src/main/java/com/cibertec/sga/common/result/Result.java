package com.cibertec.sga.common.result;

import java.util.function.Function;

/**
 * Resultado de una operación de la capa {@code application} que puede fallar con un
 * {@link DomainError} esperado, sin recurrir a excepciones para el flujo de negocio normal.
 *
 * @param <T> tipo del valor en caso de éxito
 * @param <E> tipo del error de dominio en caso de fallo
 */
public final class Result<T, E extends DomainError> {

    private final T value;
    private final E error;
    private final boolean success;

    private Result(T value, E error, boolean success) {
        this.value = value;
        this.error = error;
        this.success = success;
    }

    public static <T, E extends DomainError> Result<T, E> success(T value) {
        return new Result<>(value, null, true);
    }

    public static <T, E extends DomainError> Result<T, E> failure(E error) {
        return new Result<>(null, error, false);
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean isFailure() {
        return !success;
    }

    public T getValue() {
        if (!success) {
            throw new IllegalStateException("No se puede obtener el valor de un Result fallido");
        }
        return value;
    }

    public E getError() {
        if (success) {
            throw new IllegalStateException("No se puede obtener el error de un Result exitoso");
        }
        return error;
    }

    /**
     * Transforma el valor de un {@code Result} exitoso (ej. modelo de dominio → DTO),
     * propagando el error sin cambios si es fallido.
     */
    public <U> Result<U, E> map(Function<T, U> mapper) {
        if (isFailure()) {
            return Result.failure(error);
        }
        return Result.success(mapper.apply(value));
    }
}
