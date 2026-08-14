package com.cibertec.sga.report;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cibertec.sga.common.AbstractIntegrationTest;
import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@AutoConfigureMockMvc
@Transactional
class ReportIntegrationTest extends AbstractIntegrationTest {

    private static final String XLSX_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String authHeader;
    private String cashierAuthHeader;
    private String stage1Uuid;
    private String memberServiceUuid;
    private String stallServiceUuid;
    private String businessTypeUuid;
    private String bankUuid;
    private String incomeCategoryUuid;
    private String providerUuid;
    private String expenseReasonUuid;

    @BeforeEach
    void loginAndCreateReferences() throws Exception {
        MvcResult login = mockMvc.perform(
            post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content("""
                {"username": "admin", "password": "Admin123!"}
                """)
        ).andReturn();
        authHeader = "Bearer " + objectMapper.readTree(login.getResponse().getContentAsString()).get("accessToken").asString();

        MvcResult cashierLogin = mockMvc.perform(
            post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content("""
                {"username": "cashier", "password": "Cashier123!"}
                """)
        ).andReturn();
        cashierAuthHeader = "Bearer " + objectMapper.readTree(cashierLogin.getResponse().getContentAsString()).get("accessToken").asString();

        MvcResult stages = mockMvc.perform(get("/api/stages").header("Authorization", authHeader)).andReturn();
        stage1Uuid = findStageUuidByCode(objectMapper.readTree(stages.getResponse().getContentAsString()), 1);

        MvcResult recurrenceTypes = mockMvc.perform(get("/api/recurrence-types").header("Authorization", authHeader)).andReturn();
        String recurrenceTypeUuid = findUuidByName(objectMapper.readTree(recurrenceTypes.getResponse().getContentAsString()), "Monthly");

        MvcResult chargeTargetTypes = mockMvc.perform(get("/api/charge-target-types").header("Authorization", authHeader)).andReturn();
        JsonNode chargeTargetTypesJson = objectMapper.readTree(chargeTargetTypes.getResponse().getContentAsString());
        String memberChargeTargetTypeUuid = findUuidByName(chargeTargetTypesJson, "Member");
        String stallChargeTargetTypeUuid = findUuidByName(chargeTargetTypesJson, "Stall");

        MvcResult currencies = mockMvc.perform(get("/api/currencies").header("Authorization", authHeader)).andReturn();
        String currencyUuid = objectMapper.readTree(currencies.getResponse().getContentAsString()).get(0).get("uuid").asString();

        memberServiceUuid = createFixedCostService("Cuota Socio Reporte Test", recurrenceTypeUuid, memberChargeTargetTypeUuid, currencyUuid, "50.00");
        stallServiceUuid = createFixedCostService("Mantenimiento Reporte Test", recurrenceTypeUuid, stallChargeTargetTypeUuid, currencyUuid, "30.00");

        MvcResult businessType = mockMvc.perform(
            post("/api/business-types").header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"name": "Abarrotes Reporte Test"}
                """)
        ).andExpect(status().isCreated()).andReturn();
        businessTypeUuid = objectMapper.readTree(businessType.getResponse().getContentAsString()).get("uuid").asString();

        MvcResult bank = mockMvc.perform(
            post("/api/banks").header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"name": "BCP", "accountNumber": "REP-ACC-001", "cci": "00219400RP1", "currencyUuid": "%s"}
                """.formatted(currencyUuid))
        ).andExpect(status().isCreated()).andReturn();
        bankUuid = objectMapper.readTree(bank.getResponse().getContentAsString()).get("uuid").asString();

        MvcResult incomeCategories = mockMvc.perform(get("/api/income-categories").header("Authorization", authHeader)).andReturn();
        incomeCategoryUuid = objectMapper.readTree(incomeCategories.getResponse().getContentAsString()).get(0).get("uuid").asString();

        MvcResult provider = mockMvc.perform(
            post("/api/providers").header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"name": "Distribuidora Reporte Test", "document": "20999999002"}
                """)
        ).andExpect(status().isCreated()).andReturn();
        providerUuid = objectMapper.readTree(provider.getResponse().getContentAsString()).get("uuid").asString();

        MvcResult reasons = mockMvc.perform(get("/api/expense-reasons").header("Authorization", authHeader)).andReturn();
        expenseReasonUuid = objectMapper.readTree(reasons.getResponse().getContentAsString()).get(0).get("uuid").asString();
    }

    private String findUuidByName(JsonNode array, String name) {
        for (JsonNode node : array) {
            if (node.get("name").asString().equals(name)) {
                return node.get("uuid").asString();
            }
        }
        throw new IllegalStateException("No se encontró '" + name + "' en " + array);
    }

    private String findStageUuidByCode(JsonNode array, int code) {
        for (JsonNode node : array) {
            if (node.get("code").asInt() == code) {
                return node.get("uuid").asString();
            }
        }
        throw new IllegalStateException("No se encontró la etapa " + code + " en " + array);
    }

    private String createFixedCostService(
        String name, String recurrenceTypeUuid, String chargeTargetTypeUuid, String currencyUuid, String cost
    ) throws Exception {
        MvcResult created = mockMvc.perform(
            post("/api/services").header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"name": "%s", "recurrenceTypeUuid": "%s", "chargeTargetTypeUuid": "%s", "currencyUuid": "%s",
                 "consumptionBased": false, "cost": %s}
                """.formatted(name, recurrenceTypeUuid, chargeTargetTypeUuid, currencyUuid, cost))
        ).andExpect(status().isCreated()).andReturn();
        return objectMapper.readTree(created.getResponse().getContentAsString()).get("uuid").asString();
    }

    private String createMemberReceivable(String code, String firstName, String lastName) throws Exception {
        mockMvc.perform(
            post("/api/members").header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"code": "%s", "firstName": "%s", "lastName": "%s", "stageUuid": "%s"}
                """.formatted(code, firstName, lastName, stage1Uuid))
        ).andExpect(status().isCreated());

        MvcResult generated = mockMvc.perform(
            post("/api/account-receivables/generate-by-member")
                .header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"serviceUuid": "%s", "periodStartDate": "2026-01-01", "periodEndDate": "2026-01-31", "amount": 50.00,
                 "stageCodes": [1], "uniqueMembers": false}
                """.formatted(memberServiceUuid))
        ).andExpect(status().isCreated()).andReturn();

        JsonNode created = objectMapper.readTree(generated.getResponse().getContentAsString());
        for (JsonNode node : created) {
            if (node.get("member").get("fullName").asString().equals(firstName + " " + lastName)) {
                return node.get("uuid").asString();
            }
        }
        throw new IllegalStateException("No se encontró la cuenta por cobrar generada para " + firstName + " " + lastName);
    }

    private void createStallReceivable(String number) throws Exception {
        mockMvc.perform(
            post("/api/stalls").header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"number": "%s", "businessTypeUuid": "%s"}
                """.formatted(number, businessTypeUuid))
        ).andExpect(status().isCreated());

        mockMvc.perform(
            post("/api/account-receivables/generate-by-stall")
                .header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"serviceUuid": "%s", "periodStartDate": "2026-01-01", "periodEndDate": "2026-01-31", "amount": 30.00}
                """.formatted(stallServiceUuid))
        ).andExpect(status().isCreated());
    }

    @Test
    void reportsReflectRegisteredMovements() throws Exception {
        String paidMemberReceivable = createMemberReceivable("REP-001", "Ana", "Reyes");
        String exchangedMemberReceivable = createMemberReceivable("REP-002", "Bruno", "Vidal");
        createStallReceivable("REP-01");

        MvcResult paymentResult = mockMvc.perform(
            post("/api/payments").header("Authorization", cashierAuthHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"accountReceivableUuids": ["%s"]}
                """.formatted(paidMemberReceivable))
        ).andExpect(status().isCreated()).andReturn();
        long paymentReceiptCorrelative =
            objectMapper.readTree(paymentResult.getResponse().getContentAsString()).get("receipt").get("correlativeNumber").asLong();

        mockMvc.perform(
            post("/api/bank-exchanges").header("Authorization", cashierAuthHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"accountReceivableUuid": "%s", "bankUuid": "%s", "depositDate": "2026-02-15"}
                """.formatted(exchangedMemberReceivable, bankUuid))
        ).andExpect(status().isCreated());

        MvcResult incomeResult = mockMvc.perform(
            post("/api/incomes").header("Authorization", cashierAuthHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"depositorName": "Carla Soto", "incomeCategoryUuid": "%s", "concept": "Donación", "amount": 25.00}
                """.formatted(incomeCategoryUuid))
        ).andExpect(status().isCreated()).andReturn();
        long incomeReceiptCorrelative =
            objectMapper.readTree(incomeResult.getResponse().getContentAsString()).get("receipt").get("correlativeNumber").asLong();
        Set<Long> ownIncomeReceiptCorrelatives = Set.of(paymentReceiptCorrelative, incomeReceiptCorrelative);

        MvcResult registeredExpense = mockMvc.perform(
            post("/api/expenses").header("Authorization", cashierAuthHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"documentNumber": "REP-EXP-001", "providerUuid": "%s", "expenseDate": "2026-02-01", "amount": 80.00,
                 "expenseReasonUuid": "%s"}
                """.formatted(providerUuid, expenseReasonUuid))
        ).andExpect(status().isCreated()).andReturn();
        String expenseUuid = objectMapper.readTree(registeredExpense.getResponse().getContentAsString()).get("uuid").asString();
        mockMvc.perform(patch("/api/expenses/{uuid}/process", expenseUuid).header("Authorization", cashierAuthHeader))
            .andExpect(status().isOk());

        LocalDate today = LocalDate.now();

        XSSFWorkbook dailyMovements = downloadWorkbook(
            get("/api/reports/movements/daily").param("date", today.toString()).header("Authorization", authHeader)
        );
        assertReceiptDetailsSumTo(dailyMovements, "Income", ownIncomeReceiptCorrelatives, new BigDecimal("75.00"));
        assertTotalsInclude(dailyMovements, "BankTransaction", new BigDecimal("50.00"));
        assertTotalsInclude(dailyMovements, "Expense", new BigDecimal("80.00"));

        XSSFWorkbook monthlyMovements = downloadWorkbook(
            get("/api/reports/movements/monthly")
                .param("year", String.valueOf(today.getYear())).param("month", String.valueOf(today.getMonthValue()))
                .header("Authorization", cashierAuthHeader)
        );
        assertReceiptDetailsSumTo(monthlyMovements, "Income", ownIncomeReceiptCorrelatives, new BigDecimal("75.00"));

        XSSFWorkbook totalsMovements = downloadWorkbook(
            get("/api/reports/movements/totals").param("date", today.toString()).header("Authorization", authHeader)
        );
        assertTotalsInclude(totalsMovements, "BankTransaction", new BigDecimal("50.00"));

        XSSFWorkbook membersReport = downloadWorkbook(
            get("/api/reports/members").param("year", "2026").param("month", "1").header("Authorization", authHeader)
        );
        Sheet membersSheet = membersReport.getSheet("Reporte");
        assertThat(findRowContaining(membersSheet, "Ana Reyes")).isNotNull();
        assertThat(findRowContaining(membersSheet, "Bruno Vidal")).isNotNull();

        XSSFWorkbook nonMembersReport = downloadWorkbook(
            get("/api/reports/non-members").param("year", "2026").param("month", "1").header("Authorization", cashierAuthHeader)
        );
        Row stallRow = findRowContaining(nonMembersReport.getSheet("Reporte"), "REP-01");
        assertThat(stallRow).isNotNull();
        assertThat(stallRow.getCell(5).getStringCellValue()).isEqualTo("Pending");

        XSSFWorkbook expensesReport = downloadWorkbook(
            get("/api/reports/expenses").param("year", "2026").param("month", "2").header("Authorization", authHeader)
        );
        assertThat(findRowContaining(expensesReport.getSheet("Egresos"), "REP-EXP-001")).isNotNull();

        XSSFWorkbook banksReport = downloadWorkbook(
            get("/api/reports/banks").param("year", "2026").param("month", "2").header("Authorization", cashierAuthHeader)
        );
        assertThat(findRowContaining(banksReport.getSheet("Bancos"), "Bruno Vidal")).isNotNull();

        mockMvc.perform(get("/api/reports/movements/daily").header("Authorization", authHeader))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("REPORT_MISSING_DATE"));

        mockMvc.perform(get("/api/reports/movements/monthly").header("Authorization", authHeader))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("REPORT_MISSING_PERIOD"));

        mockMvc.perform(get("/api/reports/movements/totals").header("Authorization", authHeader))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("REPORT_INVALID_MOVEMENTS_FILTER"));

        mockMvc.perform(
            get("/api/reports/movements/totals").param("date", today.toString())
                .param("year", "2026").param("month", "1").header("Authorization", authHeader)
        )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("REPORT_INVALID_MOVEMENTS_FILTER"));

        mockMvc.perform(get("/api/reports/members").param("year", "2026").param("month", "13").header("Authorization", authHeader))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("REPORT_INVALID_PERIOD"));
    }

    @Test
    void reportEndpointsRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/reports/movements/daily").param("date", "2026-01-01")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/reports/movements/monthly").param("year", "2026").param("month", "1"))
            .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/reports/movements/totals").param("date", "2026-01-01")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/reports/members").param("year", "2026").param("month", "1")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/reports/non-members").param("year", "2026").param("month", "1")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/reports/expenses").param("year", "2026").param("month", "1")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/reports/banks").param("year", "2026").param("month", "1")).andExpect(status().isUnauthorized());
    }

    @Test
    void totalsMovementsWithInvalidMonthInPeriodReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/reports/movements/totals").param("year", "2026").param("month", "13").header("Authorization", authHeader))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("REPORT_INVALID_PERIOD"));
    }

    @Test
    void nonMembersReportMissingPeriodReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/reports/non-members").header("Authorization", authHeader))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("REPORT_MISSING_PERIOD"));
    }

    @Test
    void expensesReportMissingPeriodReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/reports/expenses").header("Authorization", authHeader))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("REPORT_MISSING_PERIOD"));
    }

    @Test
    void banksReportMissingPeriodReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/reports/banks").header("Authorization", authHeader))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("REPORT_MISSING_PERIOD"));
    }

    /**
     * Un período sin movimientos no es un error (RF-32): debe seguir devolviendo un XLSX válido,
     * solo que con el total general en cero — nunca se probó que el camino "sin filas" no rompa
     * la generación del workbook (fila de encabezados + fila de total sin ninguna fila de detalle
     * en medio).
     */
    @Test
    void dailyMovementsWithNoReceiptsReturnsEmptyWorkbookWithZeroTotal() throws Exception {
        XSSFWorkbook workbook = downloadWorkbook(
            get("/api/reports/movements/daily").param("date", "2020-01-01").header("Authorization", authHeader)
        );
        Row totalRow = findRowContaining(workbook.getSheet("Movimientos"), "TOTAL GENERAL");
        assertThat(totalRow).isNotNull();
        assertThat(totalRow.getCell(1).getNumericCellValue()).isZero();
    }

    @Test
    void membersReportWithNoReceivablesReturnsEmptyWorkbookWithZeroTotal() throws Exception {
        XSSFWorkbook workbook = downloadWorkbook(
            get("/api/reports/members").param("year", "2020").param("month", "1").header("Authorization", authHeader)
        );
        Row totalRow = findRowContaining(workbook.getSheet("Reporte"), "TOTAL");
        assertThat(totalRow).isNotNull();
        assertThat(totalRow.getCell(4).getNumericCellValue()).isZero();
    }

    private XSSFWorkbook downloadWorkbook(org.springframework.test.web.servlet.RequestBuilder request) throws Exception {
        MvcResult result = mockMvc.perform(request)
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Type", XLSX_CONTENT_TYPE))
            .andExpect(header().string("Content-Disposition", org.hamcrest.Matchers.containsString("attachment")))
            .andReturn();
        return new XSSFWorkbook(new ByteArrayInputStream(result.getResponse().getContentAsByteArray()));
    }

    private Row findRowContaining(Sheet sheet, String text) {
        for (Row row : sheet) {
            for (org.apache.poi.ss.usermodel.Cell cell : row) {
                if (cell.getCellType() == org.apache.poi.ss.usermodel.CellType.STRING && text.equals(cell.getStringCellValue())) {
                    return row;
                }
            }
        }
        return null;
    }

    /**
     * A diferencia de {@link #assertTotalsInclude}, no confía en el total agregado por tipo:
     * el reporte "Movimientos" abarca TODOS los recibos emitidos ese día/mes en el contenedor
     * Postgres compartido entre clases de test (patrón "singleton container" de
     * {@link AbstractIntegrationTest}), y {@code PaymentConcurrencyIntegrationTest} deja recibos
     * "Income" reales (no revertidos, corre sin {@code @Transactional}) fechados hoy. Suma solo
     * las filas de detalle cuyo correlativo generó este test, para no depender de qué más se
     * haya ejecutado antes en el mismo proceso.
     *
     * <p>El correlativo lo asigna una secuencia POR {@code ReceiptType} (RN-04), así que un
     * "Income" y un "Expense" registrados en el mismo test run legítimamente comparten el mismo
     * número de correlativo — filtra también por tipo, o dos recibos de tipos distintos con
     * correlativo coincidente se sumarían juntos.
     */
    private void assertReceiptDetailsSumTo(XSSFWorkbook workbook, String typeName, Set<Long> correlativeNumbers, BigDecimal expectedTotal) {
        Sheet sheet = workbook.getSheet("Movimientos");
        BigDecimal sum = BigDecimal.ZERO;
        for (Row row : sheet) {
            Cell typeCell = row.getCell(0);
            Cell correlativeCell = row.getCell(1);
            if (typeCell == null || typeCell.getCellType() != CellType.STRING || !typeName.equals(typeCell.getStringCellValue())) {
                continue;
            }
            if (correlativeCell == null || correlativeCell.getCellType() != CellType.NUMERIC) {
                continue;
            }
            if (correlativeNumbers.contains((long) correlativeCell.getNumericCellValue())) {
                sum = sum.add(BigDecimal.valueOf(row.getCell(3).getNumericCellValue()));
            }
        }
        assertThat(sum.doubleValue()).isEqualTo(expectedTotal.doubleValue());
    }

    private void assertTotalsInclude(XSSFWorkbook workbook, String typeName, BigDecimal expectedTotal) {
        Sheet sheet = workbook.getSheet("Movimientos");
        Row row = findLastRowContaining(sheet, typeName);
        assertThat(row).isNotNull();
        assertThat(row.getCell(1).getNumericCellValue()).isEqualTo(expectedTotal.doubleValue());
    }

    private Row findLastRowContaining(Sheet sheet, String text) {
        Row match = null;
        for (Row row : sheet) {
            for (org.apache.poi.ss.usermodel.Cell cell : row) {
                if (cell.getCellType() == org.apache.poi.ss.usermodel.CellType.STRING && text.equals(cell.getStringCellValue())) {
                    match = row;
                }
            }
        }
        return match;
    }
}
