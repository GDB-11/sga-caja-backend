package com.cibertec.sga.report.application;

import com.cibertec.sga.accountreceivable.domain.model.AccountReceivable;
import com.cibertec.sga.accountreceivable.domain.model.AccountReceivableMovement;
import com.cibertec.sga.accountreceivable.domain.repository.IAccountReceivableRepository;
import com.cibertec.sga.bankexchange.domain.model.BankExchange;
import com.cibertec.sga.bankexchange.domain.repository.IBankExchangeRepository;
import com.cibertec.sga.common.result.Result;
import com.cibertec.sga.expense.domain.model.Expense;
import com.cibertec.sga.expense.domain.repository.IExpenseRepository;
import com.cibertec.sga.receipt.domain.model.Receipt;
import com.cibertec.sga.receipt.domain.repository.IReceiptRepository;
import com.cibertec.sga.report.domain.error.ReportError;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Genera los reportes XLSX de movimientos (RF-32) y los reportes específicos de socios/no
 * socios/egresos/bancos (RF-33). Reutiliza los puertos de dominio de otros módulos
 * ({@code receipt}, {@code accountreceivable}, {@code expense}, {@code bankexchange}) tal como
 * ya lo hacen otros services multi-módulo del proyecto (ej. {@code AccountReceivableService}) —
 * sin persistencia propia, el módulo {@code report} es de solo lectura/composición.
 *
 * <p>"Socios"/"no socios" (RF-33) se interpretan según la exclusividad ya establecida en
 * {@code AccountReceivable} (RN-02, decidido en la Fase 4 para RF-20): cuentas cargadas
 * directamente a un {@code Member} vs. cargadas directamente a un {@code Stall}.
 */
@Service
public class ReportService implements IReportService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String[] MONTH_NAMES = {
        "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    };

    private final IReceiptRepository receiptRepository;
    private final IAccountReceivableRepository accountReceivableRepository;
    private final IExpenseRepository expenseRepository;
    private final IBankExchangeRepository bankExchangeRepository;

    public ReportService(
        IReceiptRepository receiptRepository,
        IAccountReceivableRepository accountReceivableRepository,
        IExpenseRepository expenseRepository,
        IBankExchangeRepository bankExchangeRepository
    ) {
        this.receiptRepository = receiptRepository;
        this.accountReceivableRepository = accountReceivableRepository;
        this.expenseRepository = expenseRepository;
        this.bankExchangeRepository = bankExchangeRepository;
    }

    @Override
    public Result<byte[], ReportError> dailyMovements(LocalDate date) {
        if (date == null) {
            return Result.failure(new ReportError.MissingDate());
        }
        List<Receipt> receipts = receiptRepository.findByIssueDateBetween(date, date);
        return Result.success(buildMovementsWorkbook("Movimientos del " + date.format(DATE_FORMAT), receipts, true));
    }

    @Override
    public Result<byte[], ReportError> monthlyMovements(Integer year, Integer month) {
        ReportError periodError = validatePeriod(year, month);
        if (periodError != null) {
            return Result.failure(periodError);
        }
        YearMonth yearMonth = YearMonth.of(year, month);
        List<Receipt> receipts = receiptRepository.findByIssueDateBetween(yearMonth.atDay(1), yearMonth.atEndOfMonth());
        return Result.success(buildMovementsWorkbook("Movimientos de " + monthTitle(yearMonth), receipts, true));
    }

    @Override
    public Result<byte[], ReportError> totalsMovements(LocalDate date, Integer year, Integer month) {
        boolean hasDate = date != null;
        boolean hasPeriod = year != null && month != null;
        if (hasDate == hasPeriod) {
            return Result.failure(new ReportError.InvalidMovementsFilter());
        }
        if (hasPeriod && (month < 1 || month > 12)) {
            return Result.failure(new ReportError.InvalidPeriod("El mes debe estar entre 1 y 12"));
        }

        String title;
        List<Receipt> receipts;
        if (hasDate) {
            receipts = receiptRepository.findByIssueDateBetween(date, date);
            title = "Totales del " + date.format(DATE_FORMAT);
        } else {
            YearMonth yearMonth = YearMonth.of(year, month);
            receipts = receiptRepository.findByIssueDateBetween(yearMonth.atDay(1), yearMonth.atEndOfMonth());
            title = "Totales de " + monthTitle(yearMonth);
        }
        return Result.success(buildMovementsWorkbook(title, receipts, false));
    }

    @Override
    public Result<byte[], ReportError> membersReport(Integer year, Integer month) {
        ReportError periodError = validatePeriod(year, month);
        if (periodError != null) {
            return Result.failure(periodError);
        }
        List<AccountReceivableMovement> movements = accountReceivableRepository.findMovementsByMemberPeriod(year, month);
        return Result.success(buildAccountReceivableWorkbook(
            "Reporte de socios - " + monthTitle(YearMonth.of(year, month)), "Socio", movements, true
        ));
    }

    @Override
    public Result<byte[], ReportError> nonMembersReport(Integer year, Integer month) {
        ReportError periodError = validatePeriod(year, month);
        if (periodError != null) {
            return Result.failure(periodError);
        }
        List<AccountReceivableMovement> movements = accountReceivableRepository.findMovementsByStallPeriod(year, month);
        return Result.success(buildAccountReceivableWorkbook(
            "Reporte de no socios - " + monthTitle(YearMonth.of(year, month)), "Puesto", movements, false
        ));
    }

    @Override
    public Result<byte[], ReportError> expensesReport(Integer year, Integer month) {
        ReportError periodError = validatePeriod(year, month);
        if (periodError != null) {
            return Result.failure(periodError);
        }
        List<Expense> expenses = expenseRepository.search(year, month, Pageable.unpaged()).getContent();
        return Result.success(buildExpensesWorkbook("Reporte de egresos - " + monthTitle(YearMonth.of(year, month)), expenses));
    }

    @Override
    public Result<byte[], ReportError> banksReport(Integer year, Integer month) {
        ReportError periodError = validatePeriod(year, month);
        if (periodError != null) {
            return Result.failure(periodError);
        }
        YearMonth yearMonth = YearMonth.of(year, month);
        List<BankExchange> exchanges = bankExchangeRepository.findByDepositDateBetween(yearMonth.atDay(1), yearMonth.atEndOfMonth());
        return Result.success(buildBanksWorkbook("Reporte de bancos - " + monthTitle(yearMonth), exchanges));
    }

    private ReportError validatePeriod(Integer year, Integer month) {
        if (year == null || month == null) {
            return new ReportError.MissingPeriod();
        }
        if (month < 1 || month > 12) {
            return new ReportError.InvalidPeriod("El mes debe estar entre 1 y 12");
        }
        return null;
    }

    private String monthTitle(YearMonth yearMonth) {
        return MONTH_NAMES[yearMonth.getMonthValue() - 1] + " " + yearMonth.getYear();
    }

    private byte[] buildMovementsWorkbook(String title, List<Receipt> receipts, boolean includeDetail) {
        Workbook workbook = ExcelReportWriter.newWorkbook();
        CellStyle titleStyle = ExcelReportWriter.titleStyle(workbook);
        CellStyle headerStyle = ExcelReportWriter.headerStyle(workbook);
        CellStyle currencyStyle = ExcelReportWriter.currencyStyle(workbook);
        CellStyle dateStyle = ExcelReportWriter.dateStyle(workbook);
        CellStyle totalStyle = ExcelReportWriter.totalStyle(workbook);

        Sheet sheet = workbook.createSheet("Movimientos");
        ExcelReportWriter.writeTitle(sheet, titleStyle, title);

        int rowIndex = 2;
        if (includeDetail) {
            ExcelReportWriter.writeHeaderRow(sheet, rowIndex++, headerStyle, "Tipo", "Correlativo", "Fecha", "Monto", "Moneda");
            for (Receipt receipt : receipts) {
                Row row = sheet.createRow(rowIndex++);
                ExcelReportWriter.setCell(row, 0, receipt.getReceiptType().getName());
                ExcelReportWriter.setCell(row, 1, receipt.getCorrelativeNumber());
                ExcelReportWriter.setCell(row, 2, receipt.getIssueDate(), dateStyle);
                ExcelReportWriter.setCell(row, 3, receipt.getAmount(), currencyStyle);
                ExcelReportWriter.setCell(row, 4, receipt.getCurrency().getCode());
            }
            rowIndex++;
        }

        Map<String, Map<String, BigDecimal>> totalsByTypeAndCurrency = new LinkedHashMap<>();
        Map<String, BigDecimal> grandTotalsByCurrency = new LinkedHashMap<>();
        for (Receipt receipt : receipts) {
            String currencyCode = receipt.getCurrency().getCode();
            totalsByTypeAndCurrency
                .computeIfAbsent(receipt.getReceiptType().getName(), key -> new LinkedHashMap<>())
                .merge(currencyCode, receipt.getAmount(), BigDecimal::add);
            grandTotalsByCurrency.merge(currencyCode, receipt.getAmount(), BigDecimal::add);
        }

        ExcelReportWriter.writeHeaderRow(sheet, rowIndex++, headerStyle, "Tipo", "Total", "Moneda");
        for (Map.Entry<String, Map<String, BigDecimal>> typeEntry : totalsByTypeAndCurrency.entrySet()) {
            for (Map.Entry<String, BigDecimal> currencyEntry : typeEntry.getValue().entrySet()) {
                Row row = sheet.createRow(rowIndex++);
                ExcelReportWriter.setCell(row, 0, typeEntry.getKey());
                ExcelReportWriter.setCell(row, 1, currencyEntry.getValue(), currencyStyle);
                ExcelReportWriter.setCell(row, 2, currencyEntry.getKey());
            }
        }

        if (grandTotalsByCurrency.isEmpty()) {
            Row totalRow = sheet.createRow(rowIndex);
            ExcelReportWriter.setCell(totalRow, 0, "TOTAL GENERAL");
            totalRow.getCell(0).setCellStyle(totalStyle);
            ExcelReportWriter.setCell(totalRow, 1, BigDecimal.ZERO, totalStyle);
        } else {
            for (Map.Entry<String, BigDecimal> entry : grandTotalsByCurrency.entrySet()) {
                Row totalRow = sheet.createRow(rowIndex++);
                ExcelReportWriter.setCell(totalRow, 0, "TOTAL GENERAL");
                totalRow.getCell(0).setCellStyle(totalStyle);
                ExcelReportWriter.setCell(totalRow, 1, entry.getValue(), totalStyle);
                ExcelReportWriter.setCell(totalRow, 2, entry.getKey());
            }
        }

        ExcelReportWriter.autoSizeColumns(sheet, 5);
        return ExcelReportWriter.toBytes(workbook);
    }

    private byte[] buildAccountReceivableWorkbook(
        String title, String targetLabel, List<AccountReceivableMovement> movements, boolean byMember
    ) {
        Workbook workbook = ExcelReportWriter.newWorkbook();
        CellStyle titleStyle = ExcelReportWriter.titleStyle(workbook);
        CellStyle headerStyle = ExcelReportWriter.headerStyle(workbook);
        CellStyle currencyStyle = ExcelReportWriter.currencyStyle(workbook);
        CellStyle dateStyle = ExcelReportWriter.dateStyle(workbook);
        CellStyle totalStyle = ExcelReportWriter.totalStyle(workbook);

        Sheet sheet = workbook.createSheet("Reporte");
        ExcelReportWriter.writeTitle(sheet, titleStyle, title);

        int rowIndex = 2;
        ExcelReportWriter.writeHeaderRow(
            sheet, rowIndex++, headerStyle,
            targetLabel, "Servicio", "Período inicio", "Período fin", "Monto", "Estado",
            "Forma de liquidación", "Fecha de liquidación", "Comprobante", "Moneda"
        );

        Map<String, BigDecimal> totalsByCurrency = new LinkedHashMap<>();
        for (AccountReceivableMovement movement : movements) {
            AccountReceivable accountReceivable = movement.accountReceivable();
            Row row = sheet.createRow(rowIndex++);
            ExcelReportWriter.setCell(
                row, 0, byMember ? accountReceivable.getMember().fullName() : accountReceivable.getStall().number()
            );
            ExcelReportWriter.setCell(row, 1, accountReceivable.getService().getName());
            ExcelReportWriter.setCell(row, 2, accountReceivable.getPeriodStartDate(), dateStyle);
            ExcelReportWriter.setCell(row, 3, accountReceivable.getPeriodEndDate(), dateStyle);
            ExcelReportWriter.setCell(row, 4, accountReceivable.getAmount(), currencyStyle);
            ExcelReportWriter.setCell(row, 5, accountReceivable.getStatus().getName());
            ExcelReportWriter.setCell(row, 6, movement.settlementMethod());
            ExcelReportWriter.setCell(row, 7, movement.settledDate(), dateStyle);
            ExcelReportWriter.setCell(row, 8, movement.receiptCorrelative());
            ExcelReportWriter.setCell(row, 9, accountReceivable.getCurrency().getCode());
            totalsByCurrency.merge(accountReceivable.getCurrency().getCode(), accountReceivable.getAmount(), BigDecimal::add);
        }

        if (totalsByCurrency.isEmpty()) {
            Row totalRow = sheet.createRow(rowIndex);
            ExcelReportWriter.setCell(totalRow, 3, "TOTAL");
            totalRow.getCell(3).setCellStyle(totalStyle);
            ExcelReportWriter.setCell(totalRow, 4, BigDecimal.ZERO, totalStyle);
        } else {
            for (Map.Entry<String, BigDecimal> entry : totalsByCurrency.entrySet()) {
                Row totalRow = sheet.createRow(rowIndex++);
                ExcelReportWriter.setCell(totalRow, 3, "TOTAL");
                totalRow.getCell(3).setCellStyle(totalStyle);
                ExcelReportWriter.setCell(totalRow, 4, entry.getValue(), totalStyle);
                ExcelReportWriter.setCell(totalRow, 9, entry.getKey());
            }
        }

        ExcelReportWriter.autoSizeColumns(sheet, 10);
        return ExcelReportWriter.toBytes(workbook);
    }

    private byte[] buildExpensesWorkbook(String title, List<Expense> expenses) {
        Workbook workbook = ExcelReportWriter.newWorkbook();
        CellStyle titleStyle = ExcelReportWriter.titleStyle(workbook);
        CellStyle headerStyle = ExcelReportWriter.headerStyle(workbook);
        CellStyle currencyStyle = ExcelReportWriter.currencyStyle(workbook);
        CellStyle dateStyle = ExcelReportWriter.dateStyle(workbook);
        CellStyle totalStyle = ExcelReportWriter.totalStyle(workbook);

        Sheet sheet = workbook.createSheet("Egresos");
        ExcelReportWriter.writeTitle(sheet, titleStyle, title);

        int rowIndex = 2;
        ExcelReportWriter.writeHeaderRow(
            sheet, rowIndex++, headerStyle,
            "N° Documento", "Proveedor", "Fecha", "Monto", "Documento asociado", "Motivo", "Estado", "Comprobante", "Moneda"
        );

        Map<String, BigDecimal> totalsByCurrency = new LinkedHashMap<>();
        for (Expense expense : expenses) {
            Row row = sheet.createRow(rowIndex++);
            ExcelReportWriter.setCell(row, 0, expense.getDocumentNumber());
            ExcelReportWriter.setCell(row, 1, expense.getProvider().getName());
            ExcelReportWriter.setCell(row, 2, expense.getExpenseDate(), dateStyle);
            ExcelReportWriter.setCell(row, 3, expense.getAmount(), currencyStyle);
            ExcelReportWriter.setCell(row, 4, expense.getAssociatedDocument());
            ExcelReportWriter.setCell(row, 5, expense.getExpenseReason().getName());
            ExcelReportWriter.setCell(row, 6, expense.getStatus().getName());
            ExcelReportWriter.setCell(row, 7, expense.getReceipt() == null ? null : expense.getReceipt().getCorrelativeNumber());
            ExcelReportWriter.setCell(row, 8, expense.getCurrency().getCode());
            totalsByCurrency.merge(expense.getCurrency().getCode(), expense.getAmount(), BigDecimal::add);
        }

        if (totalsByCurrency.isEmpty()) {
            Row totalRow = sheet.createRow(rowIndex);
            ExcelReportWriter.setCell(totalRow, 2, "TOTAL");
            totalRow.getCell(2).setCellStyle(totalStyle);
            ExcelReportWriter.setCell(totalRow, 3, BigDecimal.ZERO, totalStyle);
        } else {
            for (Map.Entry<String, BigDecimal> entry : totalsByCurrency.entrySet()) {
                Row totalRow = sheet.createRow(rowIndex++);
                ExcelReportWriter.setCell(totalRow, 2, "TOTAL");
                totalRow.getCell(2).setCellStyle(totalStyle);
                ExcelReportWriter.setCell(totalRow, 3, entry.getValue(), totalStyle);
                ExcelReportWriter.setCell(totalRow, 8, entry.getKey());
            }
        }

        ExcelReportWriter.autoSizeColumns(sheet, 9);
        return ExcelReportWriter.toBytes(workbook);
    }

    private byte[] buildBanksWorkbook(String title, List<BankExchange> exchanges) {
        Workbook workbook = ExcelReportWriter.newWorkbook();
        CellStyle titleStyle = ExcelReportWriter.titleStyle(workbook);
        CellStyle headerStyle = ExcelReportWriter.headerStyle(workbook);
        CellStyle currencyStyle = ExcelReportWriter.currencyStyle(workbook);
        CellStyle dateStyle = ExcelReportWriter.dateStyle(workbook);
        CellStyle totalStyle = ExcelReportWriter.totalStyle(workbook);

        Sheet sheet = workbook.createSheet("Bancos");
        ExcelReportWriter.writeTitle(sheet, titleStyle, title);

        int rowIndex = 2;
        ExcelReportWriter.writeHeaderRow(
            sheet, rowIndex++, headerStyle, "Banco", "Fecha de depósito", "Monto", "Socio/Puesto", "Comprobante", "Moneda"
        );

        Map<String, BigDecimal> totalsByCurrency = new LinkedHashMap<>();
        for (BankExchange exchange : exchanges) {
            AccountReceivable accountReceivable = exchange.getAccountReceivable();
            String target = accountReceivable.getMember() != null
                ? accountReceivable.getMember().fullName()
                : accountReceivable.getStall().number();

            Row row = sheet.createRow(rowIndex++);
            ExcelReportWriter.setCell(row, 0, exchange.getBank().getName());
            ExcelReportWriter.setCell(row, 1, exchange.getDepositDate(), dateStyle);
            ExcelReportWriter.setCell(row, 2, exchange.getAmount(), currencyStyle);
            ExcelReportWriter.setCell(row, 3, target);
            ExcelReportWriter.setCell(row, 4, exchange.getReceipt().getCorrelativeNumber());
            ExcelReportWriter.setCell(row, 5, exchange.getCurrency().getCode());
            totalsByCurrency.merge(exchange.getCurrency().getCode(), exchange.getAmount(), BigDecimal::add);
        }

        if (totalsByCurrency.isEmpty()) {
            Row totalRow = sheet.createRow(rowIndex);
            ExcelReportWriter.setCell(totalRow, 1, "TOTAL");
            totalRow.getCell(1).setCellStyle(totalStyle);
            ExcelReportWriter.setCell(totalRow, 2, BigDecimal.ZERO, totalStyle);
        } else {
            for (Map.Entry<String, BigDecimal> entry : totalsByCurrency.entrySet()) {
                Row totalRow = sheet.createRow(rowIndex++);
                ExcelReportWriter.setCell(totalRow, 1, "TOTAL");
                totalRow.getCell(1).setCellStyle(totalStyle);
                ExcelReportWriter.setCell(totalRow, 2, entry.getValue(), totalStyle);
                ExcelReportWriter.setCell(totalRow, 5, entry.getKey());
            }
        }

        ExcelReportWriter.autoSizeColumns(sheet, 6);
        return ExcelReportWriter.toBytes(workbook);
    }
}
