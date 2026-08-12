package com.cibertec.sga.bank.domain.error;

import com.cibertec.sga.common.result.DomainError;
import com.cibertec.sga.common.result.ErrorType;

/**
 * Errores de negocio esperados del módulo {@code Bank}.
 */
public sealed interface BankError extends DomainError
    permits BankError.NotFound, BankError.DuplicateAccountNumber, BankError.CurrencyNotFound {

    record NotFound(String uuid) implements BankError {
        @Override
        public String code() {
            return "BANK_NOT_FOUND";
        }

        @Override
        public String message() {
            return "Banco no encontrado: " + uuid;
        }

        @Override
        public ErrorType type() {
            return ErrorType.NOT_FOUND;
        }
    }

    record DuplicateAccountNumber(String accountNumber) implements BankError {
        @Override
        public String code() {
            return "BANK_DUPLICATE_ACCOUNT_NUMBER";
        }

        @Override
        public String message() {
            return "Ya existe un banco con número de cuenta '" + accountNumber + "'";
        }

        @Override
        public ErrorType type() {
            return ErrorType.CONFLICT;
        }
    }

    record CurrencyNotFound(String uuid) implements BankError {
        @Override
        public String code() {
            return "BANK_CURRENCY_NOT_FOUND";
        }

        @Override
        public String message() {
            return "Moneda no encontrada: " + uuid;
        }

        @Override
        public ErrorType type() {
            return ErrorType.VALIDATION;
        }
    }
}
