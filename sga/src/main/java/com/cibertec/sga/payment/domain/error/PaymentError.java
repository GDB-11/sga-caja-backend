package com.cibertec.sga.payment.domain.error;

import com.cibertec.sga.common.result.DomainError;
import com.cibertec.sga.common.result.ErrorType;

/**
 * Errores de negocio esperados del módulo {@code Payment}.
 */
public sealed interface PaymentError extends DomainError
    permits PaymentError.NotFound, PaymentError.EmptySelection, PaymentError.AccountReceivableNotFound,
    PaymentError.AccountReceivableNotPending {

    record NotFound(String uuid) implements PaymentError {
        @Override
        public String code() {
            return "PAYMENT_NOT_FOUND";
        }

        @Override
        public String message() {
            return "Pago no encontrado: " + uuid;
        }

        @Override
        public ErrorType type() {
            return ErrorType.NOT_FOUND;
        }
    }

    record EmptySelection() implements PaymentError {
        @Override
        public String code() {
            return "PAYMENT_EMPTY_SELECTION";
        }

        @Override
        public String message() {
            return "Debe seleccionar al menos una cuenta por cobrar";
        }

        @Override
        public ErrorType type() {
            return ErrorType.VALIDATION;
        }
    }

    record AccountReceivableNotFound(String uuid) implements PaymentError {
        @Override
        public String code() {
            return "PAYMENT_ACCOUNT_RECEIVABLE_NOT_FOUND";
        }

        @Override
        public String message() {
            return "Cuenta por cobrar no encontrada: " + uuid;
        }

        @Override
        public ErrorType type() {
            return ErrorType.VALIDATION;
        }
    }

    record AccountReceivableNotPending(String uuid) implements PaymentError {
        @Override
        public String code() {
            return "PAYMENT_ACCOUNT_RECEIVABLE_NOT_PENDING";
        }

        @Override
        public String message() {
            return "La cuenta por cobrar no está pendiente: " + uuid;
        }

        @Override
        public ErrorType type() {
            return ErrorType.CONFLICT;
        }
    }
}
