package com.cibertec.sga.report.application;

import com.cibertec.sga.common.result.Result;
import com.cibertec.sga.report.domain.error.ReportError;
import java.time.LocalDate;

/**
 * Caso de uso de generación de reportes XLSX (RF-32, RF-33). Cada método devuelve el archivo ya
 * generado como {@code byte[]} — el controller solo agrega los encabezados HTTP de descarga.
 */
public interface IReportService {

    /**
     * Movimientos (comprobantes de ingreso, egreso y bancarios) de un día puntual (RF-32).
     */
    Result<byte[], ReportError> dailyMovements(LocalDate date);

    /**
     * Movimientos de un mes completo (RF-32).
     */
    Result<byte[], ReportError> monthlyMovements(Integer year, Integer month);

    /**
     * Totales por tipo de movimiento (sin detalle línea a línea), para un día o un mes — exactamente
     * uno de los dos filtros, según RN-07 (RF-32).
     */
    Result<byte[], ReportError> totalsMovements(LocalDate date, Integer year, Integer month);

    /**
     * Cuentas por cobrar cargadas a socios en el mes indicado (RF-33).
     */
    Result<byte[], ReportError> membersReport(Integer year, Integer month);

    /**
     * Cuentas por cobrar cargadas a puestos (no socios) en el mes indicado (RF-33).
     */
    Result<byte[], ReportError> nonMembersReport(Integer year, Integer month);

    /**
     * Egresos del mes indicado (RF-33).
     */
    Result<byte[], ReportError> expensesReport(Integer year, Integer month);

    /**
     * Canjes bancarios del mes indicado (RF-33).
     */
    Result<byte[], ReportError> banksReport(Integer year, Integer month);
}
