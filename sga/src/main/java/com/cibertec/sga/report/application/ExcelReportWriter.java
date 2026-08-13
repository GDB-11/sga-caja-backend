package com.cibertec.sga.report.application;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Utilidades compartidas para construir los libros XLSX de los reportes (RF-32, RF-33) con
 * Apache POI — estilos consistentes de encabezado/moneda/fecha y helpers de escritura de celda,
 * sin repetirlos en cada constructor de reporte de {@link ReportService}.
 */
final class ExcelReportWriter {

    private ExcelReportWriter() {
    }

    static Workbook newWorkbook() {
        return new XSSFWorkbook();
    }

    static CellStyle titleStyle(Workbook workbook) {
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        CellStyle style = workbook.createCellStyle();
        style.setFont(font);
        return style;
    }

    static CellStyle headerStyle(Workbook workbook) {
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        CellStyle style = workbook.createCellStyle();
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    static CellStyle totalStyle(Workbook workbook) {
        Font font = workbook.createFont();
        font.setBold(true);
        CellStyle style = workbook.createCellStyle();
        style.setFont(font);
        style.setDataFormat(workbook.createDataFormat().getFormat("#,##0.00"));
        return style;
    }

    static CellStyle currencyStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat("#,##0.00"));
        return style;
    }

    static CellStyle dateStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat("dd/mm/yyyy"));
        return style;
    }

    static void writeTitle(Sheet sheet, CellStyle style, String title) {
        Row row = sheet.createRow(0);
        Cell cell = row.createCell(0);
        cell.setCellValue(title);
        cell.setCellStyle(style);
    }

    static Row writeHeaderRow(Sheet sheet, int rowIndex, CellStyle style, String... headers) {
        Row row = sheet.createRow(rowIndex);
        for (int col = 0; col < headers.length; col++) {
            Cell cell = row.createCell(col);
            cell.setCellValue(headers[col]);
            cell.setCellStyle(style);
        }
        return row;
    }

    static void setCell(Row row, int col, String value) {
        row.createCell(col).setCellValue(value == null ? "" : value);
    }

    static void setCell(Row row, int col, Long value) {
        Cell cell = row.createCell(col);
        if (value != null) {
            cell.setCellValue(value);
        }
    }

    static void setCell(Row row, int col, LocalDate value, CellStyle style) {
        Cell cell = row.createCell(col);
        if (value != null) {
            cell.setCellValue(value);
            cell.setCellStyle(style);
        }
    }

    static void setCell(Row row, int col, BigDecimal value, CellStyle style) {
        Cell cell = row.createCell(col);
        if (value != null) {
            cell.setCellValue(value.doubleValue());
        }
        cell.setCellStyle(style);
    }

    static void autoSizeColumns(Sheet sheet, int columnCount) {
        for (int col = 0; col < columnCount; col++) {
            sheet.autoSizeColumn(col);
        }
    }

    static byte[] toBytes(Workbook workbook) {
        try (workbook; ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo generar el archivo XLSX: " + e.getMessage(), e);
        }
    }
}
