package com.cibertec.sga.income.domain.error;

import com.cibertec.sga.common.result.DomainError;
import com.cibertec.sga.common.result.ErrorType;

/**
 * Errores de negocio esperados del módulo {@code Income}.
 */
public sealed interface IncomeError
    extends DomainError permits IncomeError.NotFound, IncomeError.IncomeCategoryNotFound, IncomeError.CurrencyNotFound {

    record NotFound(String uuid) implements IncomeError {
        @Override
        public String code() {
            return "INCOME_NOT_FOUND";
        }

        @Override
        public String message() {
            return "Ingreso no encontrado: " + uuid;
        }

        @Override
        public ErrorType type() {
            return ErrorType.NOT_FOUND;
        }
    }

    record IncomeCategoryNotFound(String uuid) implements IncomeError {
        @Override
        public String code() {
            return "INCOME_CATEGORY_NOT_FOUND";
        }

        @Override
        public String message() {
            return "Categoría de ingreso no encontrada: " + uuid;
        }

        @Override
        public ErrorType type() {
            return ErrorType.VALIDATION;
        }
    }

    record CurrencyNotFound(String uuid) implements IncomeError {
        @Override
        public String code() {
            return "INCOME_CURRENCY_NOT_FOUND";
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
