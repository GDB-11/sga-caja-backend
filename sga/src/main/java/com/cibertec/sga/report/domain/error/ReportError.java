package com.cibertec.sga.report.domain.error;

import com.cibertec.sga.common.result.DomainError;
import com.cibertec.sga.common.result.ErrorType;

/**
 * Errores de negocio esperados del módulo {@code Report} (RF-32, RF-33, RN-07).
 */
public sealed interface ReportError extends DomainError
    permits ReportError.MissingDate, ReportError.MissingPeriod, ReportError.InvalidPeriod, ReportError.InvalidMovementsFilter {

    record MissingDate() implements ReportError {
        @Override
        public String code() {
            return "REPORT_MISSING_DATE";
        }

        @Override
        public String message() {
            return "La fecha es obligatoria para este reporte";
        }

        @Override
        public ErrorType type() {
            return ErrorType.VALIDATION;
        }
    }

    record MissingPeriod() implements ReportError {
        @Override
        public String code() {
            return "REPORT_MISSING_PERIOD";
        }

        @Override
        public String message() {
            return "El año y el mes son obligatorios para este reporte";
        }

        @Override
        public ErrorType type() {
            return ErrorType.VALIDATION;
        }
    }

    record InvalidPeriod(String detail) implements ReportError {
        @Override
        public String code() {
            return "REPORT_INVALID_PERIOD";
        }

        @Override
        public String message() {
            return detail;
        }

        @Override
        public ErrorType type() {
            return ErrorType.VALIDATION;
        }
    }

    record InvalidMovementsFilter() implements ReportError {
        @Override
        public String code() {
            return "REPORT_INVALID_MOVEMENTS_FILTER";
        }

        @Override
        public String message() {
            return "Indique exactamente uno: fecha (reporte diario) o año/mes (reporte mensual)";
        }

        @Override
        public ErrorType type() {
            return ErrorType.VALIDATION;
        }
    }
}
