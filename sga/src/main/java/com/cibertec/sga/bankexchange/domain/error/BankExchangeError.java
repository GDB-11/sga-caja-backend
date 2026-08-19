package com.cibertec.sga.bankexchange.domain.error;

import com.cibertec.sga.common.result.DomainError;
import com.cibertec.sga.common.result.ErrorType;

/**
 * Errores de negocio esperados del módulo {@code BankExchange}.
 */
public sealed interface BankExchangeError extends DomainError
    permits BankExchangeError.NotFound, BankExchangeError.AccountReceivableNotFound,
    BankExchangeError.AccountReceivableNotMemberTarget, BankExchangeError.AccountReceivableNotPending,
    BankExchangeError.BankNotFound, BankExchangeError.BankInactive, BankExchangeError.CurrencyMismatch {

    record NotFound(String uuid) implements BankExchangeError {
        @Override
        public String code() {
            return "BANK_EXCHANGE_NOT_FOUND";
        }

        @Override
        public String message() {
            return "Canje bancario no encontrado: " + uuid;
        }

        @Override
        public ErrorType type() {
            return ErrorType.NOT_FOUND;
        }
    }

    record AccountReceivableNotFound(String uuid) implements BankExchangeError {
        @Override
        public String code() {
            return "BANK_EXCHANGE_ACCOUNT_RECEIVABLE_NOT_FOUND";
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

    record AccountReceivableNotMemberTarget(String uuid) implements BankExchangeError {
        @Override
        public String code() {
            return "BANK_EXCHANGE_ACCOUNT_RECEIVABLE_NOT_MEMBER_TARGET";
        }

        @Override
        public String message() {
            return "Solo se pueden canjear cuentas por cobrar de un socio: " + uuid;
        }

        @Override
        public ErrorType type() {
            return ErrorType.VALIDATION;
        }
    }

    record AccountReceivableNotPending(String uuid) implements BankExchangeError {
        @Override
        public String code() {
            return "BANK_EXCHANGE_ACCOUNT_RECEIVABLE_NOT_PENDING";
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

    record BankNotFound(String uuid) implements BankExchangeError {
        @Override
        public String code() {
            return "BANK_EXCHANGE_BANK_NOT_FOUND";
        }

        @Override
        public String message() {
            return "Banco no encontrado: " + uuid;
        }

        @Override
        public ErrorType type() {
            return ErrorType.VALIDATION;
        }
    }

    record BankInactive(String uuid) implements BankExchangeError {
        @Override
        public String code() {
            return "BANK_EXCHANGE_BANK_INACTIVE";
        }

        @Override
        public String message() {
            return "El banco está inactivo: " + uuid;
        }

        @Override
        public ErrorType type() {
            return ErrorType.VALIDATION;
        }
    }

    record CurrencyMismatch(String accountReceivableCurrency, String bankCurrency) implements BankExchangeError {
        @Override
        public String code() {
            return "BANK_EXCHANGE_CURRENCY_MISMATCH";
        }

        @Override
        public String message() {
            return "La moneda de la cuenta por cobrar (" + accountReceivableCurrency
                + ") no coincide con la moneda del banco (" + bankCurrency + ")";
        }

        @Override
        public ErrorType type() {
            return ErrorType.VALIDATION;
        }
    }
}
