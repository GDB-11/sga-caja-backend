package com.cibertec.sga.report.web;

import com.cibertec.sga.common.result.Result;
import com.cibertec.sga.common.result.ResultResponse;
import com.cibertec.sga.report.application.IReportService;
import com.cibertec.sga.report.domain.error.ReportError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints de descarga de reportes XLSX (RF-32, RF-33, CU-04) — sin restricción de rol, ambos
 * roles pueden descargarlos según la matriz RBAC del plan.
 */
@RestController
@RequestMapping("/api/reports")
@Tag(name = "Reportes", description = "Descarga de reportes XLSX de movimientos, socios, no socios, egresos y bancos")
public class ReportController {

    private static final MediaType XLSX = MediaType.parseMediaType(
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    );

    private final IReportService reportService;

    public ReportController(IReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/movements/daily")
    @Operation(summary = "Reporte de movimientos diarios (RF-32)")
    public ResponseEntity<?> dailyMovements(
        @RequestParam(required = false) LocalDate date,
        HttpServletRequest request
    ) {
        return download(reportService.dailyMovements(date), "movimientos-diarios-" + date + ".xlsx", request);
    }

    @GetMapping("/movements/monthly")
    @Operation(summary = "Reporte de movimientos mensuales (RF-32)")
    public ResponseEntity<?> monthlyMovements(
        @RequestParam(required = false) Integer year, @RequestParam(required = false) Integer month,
        HttpServletRequest request
    ) {
        return download(reportService.monthlyMovements(year, month), "movimientos-mensuales-" + year + "-" + month + ".xlsx", request);
    }

    @GetMapping("/movements/totals")
    @Operation(summary = "Reporte de totales de movimientos, por día o por mes (RF-32, RN-07)")
    public ResponseEntity<?> totalsMovements(
        @RequestParam(required = false) LocalDate date,
        @RequestParam(required = false) Integer year, @RequestParam(required = false) Integer month,
        HttpServletRequest request
    ) {
        String period = date != null ? date.toString() : year + "-" + month;
        return download(reportService.totalsMovements(date, year, month), "totales-movimientos-" + period + ".xlsx", request);
    }

    @GetMapping("/members")
    @Operation(summary = "Reporte de cuentas por cobrar de socios (RF-33)")
    public ResponseEntity<?> membersReport(
        @RequestParam(required = false) Integer year, @RequestParam(required = false) Integer month,
        HttpServletRequest request
    ) {
        return download(reportService.membersReport(year, month), "reporte-socios-" + year + "-" + month + ".xlsx", request);
    }

    @GetMapping("/non-members")
    @Operation(summary = "Reporte de cuentas por cobrar de no socios (RF-33)")
    public ResponseEntity<?> nonMembersReport(
        @RequestParam(required = false) Integer year, @RequestParam(required = false) Integer month,
        HttpServletRequest request
    ) {
        return download(reportService.nonMembersReport(year, month), "reporte-no-socios-" + year + "-" + month + ".xlsx", request);
    }

    @GetMapping("/expenses")
    @Operation(summary = "Reporte de egresos (RF-33)")
    public ResponseEntity<?> expensesReport(
        @RequestParam(required = false) Integer year, @RequestParam(required = false) Integer month,
        HttpServletRequest request
    ) {
        return download(reportService.expensesReport(year, month), "reporte-egresos-" + year + "-" + month + ".xlsx", request);
    }

    @GetMapping("/banks")
    @Operation(summary = "Reporte de canjes bancarios (RF-33)")
    public ResponseEntity<?> banksReport(
        @RequestParam(required = false) Integer year, @RequestParam(required = false) Integer month,
        HttpServletRequest request
    ) {
        return download(reportService.banksReport(year, month), "reporte-bancos-" + year + "-" + month + ".xlsx", request);
    }

    private ResponseEntity<?> download(Result<byte[], ReportError> result, String filename, HttpServletRequest request) {
        if (result.isFailure()) {
            return ResultResponse.toResponseEntity(result, HttpStatus.OK, request);
        }
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
            .contentType(XLSX)
            .body(result.getValue());
    }
}
