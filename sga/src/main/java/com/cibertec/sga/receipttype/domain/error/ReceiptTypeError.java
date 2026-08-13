package com.cibertec.sga.receipttype.domain.error;

import com.cibertec.sga.common.result.DomainError;
import com.cibertec.sga.common.result.ErrorType;

/**
 * Errores de negocio esperados del módulo {@code ReceiptType}.
 */
public sealed interface ReceiptTypeError extends DomainError permits ReceiptTypeError.NotFound {

    record NotFound(String uuid) implements ReceiptTypeError {
        @Override
        public String code() {
            return "RECEIPT_TYPE_NOT_FOUND";
        }

        @Override
        public String message() {
            return "Tipo de comprobante no encontrado: " + uuid;
        }

        @Override
        public ErrorType type() {
            return ErrorType.NOT_FOUND;
        }
    }
}
