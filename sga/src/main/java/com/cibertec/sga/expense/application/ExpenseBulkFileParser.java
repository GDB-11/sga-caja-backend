package com.cibertec.sga.expense.application;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 * Lee un archivo XLSX de carga masiva de egresos (RF-28) — formato elegido en vez de CSV para
 * evitar el choque coma-decimal/coma-delimitador de CSV en locale es-PE (decidido 2026-08-09).
 * Columnas esperadas, en orden, con encabezado en la primera fila: {@code DocumentNumber},
 * {@code ProviderName}, {@code ExpenseDate}, {@code Amount}, {@code AssociatedDocument}
 * (opcional), {@code ExpenseReason}, {@code Moneda} (código de moneda, ej. {@code PEN}/{@code
 * USD} — la moneda no tiene una fuente natural para un egreso, a diferencia de las cuentas por
 * cobrar que la heredan del {@code Service}).
 */
final class ExpenseBulkFileParser {

    private static final int COL_DOCUMENT_NUMBER = 0;
    private static final int COL_PROVIDER_NAME = 1;
    private static final int COL_EXPENSE_DATE = 2;
    private static final int COL_AMOUNT = 3;
    private static final int COL_ASSOCIATED_DOCUMENT = 4;
    private static final int COL_EXPENSE_REASON = 5;
    private static final int COL_CURRENCY_CODE = 6;

    private ExpenseBulkFileParser() {
    }

    static BulkParseResult parse(InputStream inputStream) {
        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet.getLastRowNum() < 1) {
                throw new IllegalArgumentException("El archivo no contiene filas de datos");
            }

            List<BulkExpenseRow> rows = new ArrayList<>();
            List<String> errors = new ArrayList<>();

            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null || isBlankRow(row)) {
                    continue;
                }
                readRow(row, errors).ifPresent(rows::add);
            }

            return new BulkParseResult(rows, errors);
        } catch (IOException e) {
            throw new IllegalArgumentException("No se pudo leer el archivo: " + e.getMessage(), e);
        }
    }

    private static boolean isBlankRow(Row row) {
        for (int col = 0; col <= COL_CURRENCY_CODE; col++) {
            Cell cell = row.getCell(col);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }

    private static Optional<BulkExpenseRow> readRow(Row row, List<String> errors) {
        int excelRowNumber = row.getRowNum() + 1;
        try {
            String documentNumber = readString(row, COL_DOCUMENT_NUMBER);
            String providerName = readString(row, COL_PROVIDER_NAME);
            LocalDate expenseDate = readDate(row, COL_EXPENSE_DATE);
            BigDecimal amount = readAmount(row, COL_AMOUNT);
            String associatedDocument = readString(row, COL_ASSOCIATED_DOCUMENT);
            String expenseReasonName = readString(row, COL_EXPENSE_REASON);
            String currencyCode = readString(row, COL_CURRENCY_CODE);

            String blankFieldError = firstBlankFieldError(
                excelRowNumber, documentNumber, providerName, expenseDate, amount, expenseReasonName, currencyCode
            );
            if (blankFieldError != null) {
                errors.add(blankFieldError);
                return Optional.empty();
            }

            return Optional.of(new BulkExpenseRow(
                excelRowNumber, documentNumber, providerName, expenseDate, amount, associatedDocument, expenseReasonName, currencyCode
            ));
        } catch (RuntimeException e) {
            errors.add("Fila " + excelRowNumber + ": " + e.getMessage());
            return Optional.empty();
        }
    }

    private static String firstBlankFieldError(
        int excelRowNumber, String documentNumber, String providerName, LocalDate expenseDate,
        BigDecimal amount, String expenseReasonName, String currencyCode
    ) {
        if (documentNumber == null || documentNumber.isBlank()) {
            return "Fila " + excelRowNumber + ": el número de documento es obligatorio";
        }
        if (providerName == null || providerName.isBlank()) {
            return "Fila " + excelRowNumber + ": el proveedor es obligatorio";
        }
        if (expenseDate == null) {
            return "Fila " + excelRowNumber + ": la fecha de egreso es obligatoria";
        }
        if (amount == null || amount.signum() <= 0) {
            return "Fila " + excelRowNumber + ": el monto debe ser mayor a cero";
        }
        if (expenseReasonName == null || expenseReasonName.isBlank()) {
            return "Fila " + excelRowNumber + ": el motivo del egreso es obligatorio";
        }
        if (currencyCode == null || currencyCode.isBlank()) {
            return "Fila " + excelRowNumber + ": la moneda es obligatoria";
        }
        return null;
    }

    private static String readString(Row row, int col) {
        Cell cell = row.getCell(col);
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return null;
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf(cell.getNumericCellValue());
        }
        String value = cell.getStringCellValue();
        return value == null ? null : value.trim();
    }

    private static LocalDate readDate(Row row, int col) {
        Cell cell = row.getCell(col);
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return null;
        }
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getLocalDateTimeCellValue().toLocalDate();
        }
        return LocalDate.parse(cell.getStringCellValue().trim());
    }

    private static BigDecimal readAmount(Row row, int col) {
        Cell cell = row.getCell(col);
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return null;
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            return BigDecimal.valueOf(cell.getNumericCellValue());
        }
        return new BigDecimal(cell.getStringCellValue().trim());
    }
}
