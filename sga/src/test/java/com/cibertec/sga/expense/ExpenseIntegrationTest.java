package com.cibertec.sga.expense;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cibertec.sga.common.AbstractIntegrationTest;
import java.io.ByteArrayOutputStream;
import java.util.UUID;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@AutoConfigureMockMvc
@Transactional
class ExpenseIntegrationTest extends AbstractIntegrationTest {

    private static final String BASE_URL = "/api/expenses";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String authHeader;
    private String cashierAuthHeader;
    private String providerUuid;
    private String expenseReasonUuid;
    private String currencyUuid;
    private String currencyCode;

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

        MvcResult provider = mockMvc.perform(
            post("/api/providers").header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"name": "Distribuidora Expense Test", "document": "20999999001"}
                """)
        ).andExpect(status().isCreated()).andReturn();
        providerUuid = objectMapper.readTree(provider.getResponse().getContentAsString()).get("uuid").asString();

        MvcResult reasons = mockMvc.perform(get("/api/expense-reasons").header("Authorization", authHeader)).andReturn();
        expenseReasonUuid = objectMapper.readTree(reasons.getResponse().getContentAsString()).get(0).get("uuid").asString();

        MvcResult currencies = mockMvc.perform(get("/api/currencies").header("Authorization", authHeader)).andReturn();
        JsonNode currency = objectMapper.readTree(currencies.getResponse().getContentAsString()).get(0);
        currencyUuid = currency.get("uuid").asString();
        currencyCode = currency.get("code").asString();
    }

    private String registerExpense(String documentNumber) throws Exception {
        MvcResult created = mockMvc.perform(
            post(BASE_URL).header("Authorization", cashierAuthHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"documentNumber": "%s", "providerUuid": "%s", "expenseDate": "2026-02-01", "amount": 150.00,
                 "associatedDocument": "OC-001", "expenseReasonUuid": "%s", "currencyUuid": "%s"}
                """.formatted(documentNumber, providerUuid, expenseReasonUuid, currencyUuid))
        ).andExpect(status().isCreated()).andReturn();
        return objectMapper.readTree(created.getResponse().getContentAsString()).get("uuid").asString();
    }

    @Test
    void registerThenAppearsInListAndGetByUuid() throws Exception {
        String uuid = registerExpense("EXP-001");

        mockMvc.perform(get(BASE_URL + "/{uuid}", uuid).header("Authorization", cashierAuthHeader))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status.name").value("Pending"))
            .andExpect(jsonPath("$.provider.uuid").value(providerUuid))
            .andExpect(jsonPath("$.receipt").doesNotExist());

        mockMvc.perform(get(BASE_URL).param("year", "2026").param("month", "2").header("Authorization", cashierAuthHeader))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(1));
    }

    @Test
    void registerRecordsActingUserForAudit() throws Exception {
        String uuid = registerExpense("EXP-AUDIT-001");

        mockMvc.perform(get(BASE_URL + "/{uuid}", uuid).header("Authorization", cashierAuthHeader))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.createdBy.username").value("cashier"))
            .andExpect(jsonPath("$.createdBy.uuid").isNotEmpty());
    }

    @Test
    void registerWithAdministratorRoleReturnsForbidden() throws Exception {
        mockMvc.perform(
            post(BASE_URL).header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"documentNumber": "EXP-002", "providerUuid": "%s", "expenseDate": "2026-02-01", "amount": 150.00,
                 "expenseReasonUuid": "%s", "currencyUuid": "%s"}
                """.formatted(providerUuid, expenseReasonUuid, currencyUuid))
        ).andExpect(status().isForbidden());
    }

    @Test
    void registerWithUnknownProviderReturnsBadRequest() throws Exception {
        mockMvc.perform(
            post(BASE_URL).header("Authorization", cashierAuthHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"documentNumber": "EXP-003", "providerUuid": "%s", "expenseDate": "2026-02-01", "amount": 150.00,
                 "expenseReasonUuid": "%s", "currencyUuid": "%s"}
                """.formatted(UUID.randomUUID(), expenseReasonUuid, currencyUuid))
        )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("EXPENSE_PROVIDER_NOT_FOUND"));
    }

    @Test
    void registerWithUnknownCurrencyReturnsBadRequest() throws Exception {
        mockMvc.perform(
            post(BASE_URL).header("Authorization", cashierAuthHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"documentNumber": "EXP-003B", "providerUuid": "%s", "expenseDate": "2026-02-01", "amount": 150.00,
                 "expenseReasonUuid": "%s", "currencyUuid": "%s"}
                """.formatted(providerUuid, expenseReasonUuid, UUID.randomUUID()))
        )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("EXPENSE_CURRENCY_NOT_FOUND"));
    }

    @Test
    void voidPendingExpenseFlipsStatusToVoided() throws Exception {
        String uuid = registerExpense("EXP-004");

        mockMvc.perform(patch(BASE_URL + "/{uuid}/void", uuid).header("Authorization", cashierAuthHeader))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status.name").value("Voided"));
    }

    @Test
    void voidAlreadyVoidedExpenseReturnsConflict() throws Exception {
        String uuid = registerExpense("EXP-005");
        mockMvc.perform(patch(BASE_URL + "/{uuid}/void", uuid).header("Authorization", cashierAuthHeader)).andExpect(status().isOk());

        mockMvc.perform(patch(BASE_URL + "/{uuid}/void", uuid).header("Authorization", cashierAuthHeader))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.error").value("EXPENSE_NOT_PENDING"));
    }

    @Test
    void processPendingExpenseEmitsReceiptAndMarksProcessed() throws Exception {
        String uuid = registerExpense("EXP-006");

        mockMvc.perform(patch(BASE_URL + "/{uuid}/process", uuid).header("Authorization", cashierAuthHeader))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status.name").value("Processed"))
            .andExpect(jsonPath("$.receipt.receiptTypeName").value("Expense"))
            .andExpect(jsonPath("$.receipt.correlativeNumber").isNumber());
    }

    @Test
    void processVoidedExpenseReturnsConflict() throws Exception {
        String uuid = registerExpense("EXP-007");
        mockMvc.perform(patch(BASE_URL + "/{uuid}/void", uuid).header("Authorization", cashierAuthHeader)).andExpect(status().isOk());

        mockMvc.perform(patch(BASE_URL + "/{uuid}/process", uuid).header("Authorization", cashierAuthHeader))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.error").value("EXPENSE_NOT_PENDING"));
    }

    @Test
    void bulkUploadWithValidRowsCreatesExpensesPendingWithoutReceipt() throws Exception {
        MvcResult reasons = mockMvc.perform(get("/api/expense-reasons").header("Authorization", authHeader)).andReturn();
        String reasonName = objectMapper.readTree(reasons.getResponse().getContentAsString()).get(0).get("name").asString();

        byte[] xlsx = buildBulkExpenseWorkbook(new String[][] {
            {"BULK-001", "Distribuidora Expense Test", "2026-02-10", "80.00", "OC-100", reasonName, currencyCode},
            {"BULK-002", "Distribuidora Expense Test", "2026-02-11", "120.50", "", reasonName, currencyCode}
        });
        MockMultipartFile file = new MockMultipartFile("file", "egresos.xlsx", "application/vnd.ms-excel", xlsx);

        MvcResult uploaded = mockMvc.perform(multipart(BASE_URL + "/bulk-upload").file(file).header("Authorization", cashierAuthHeader))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].bulkUpload.fileName").value("egresos.xlsx"))
            .andExpect(jsonPath("$[0].status.name").value("Pending"))
            .andReturn();

        JsonNode created = objectMapper.readTree(uploaded.getResponse().getContentAsString());
        String firstUuid = created.get(0).get("uuid").asString();
        mockMvc.perform(get(BASE_URL + "/{uuid}", firstUuid).header("Authorization", cashierAuthHeader))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.receipt").doesNotExist());
    }

    @Test
    void bulkUploadWithUnknownProviderReturnsBadRequestAndCreatesNoExpenses() throws Exception {
        MvcResult reasons = mockMvc.perform(get("/api/expense-reasons").header("Authorization", authHeader)).andReturn();
        String reasonName = objectMapper.readTree(reasons.getResponse().getContentAsString()).get(0).get("name").asString();

        byte[] xlsx = buildBulkExpenseWorkbook(new String[][] {
            {"BULK-003", "Proveedor Inexistente SAC", "2026-02-12", "50.00", "", reasonName, currencyCode}
        });
        MockMultipartFile file = new MockMultipartFile("file", "egresos-malos.xlsx", "application/vnd.ms-excel", xlsx);

        mockMvc.perform(multipart(BASE_URL + "/bulk-upload").file(file).header("Authorization", cashierAuthHeader))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("EXPENSE_BULK_VALIDATION_FAILED"));

        mockMvc.perform(get(BASE_URL).param("year", "2026").param("month", "2").header("Authorization", cashierAuthHeader))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(0));
    }

    @Test
    void bulkUploadWithUnknownCurrencyCodeReturnsBadRequestAndCreatesNoExpenses() throws Exception {
        MvcResult reasons = mockMvc.perform(get("/api/expense-reasons").header("Authorization", authHeader)).andReturn();
        String reasonName = objectMapper.readTree(reasons.getResponse().getContentAsString()).get(0).get("name").asString();

        byte[] xlsx = buildBulkExpenseWorkbook(new String[][] {
            {"BULK-004", "Distribuidora Expense Test", "2026-02-13", "60.00", "", reasonName, "ZZZ"}
        });
        MockMultipartFile file = new MockMultipartFile("file", "egresos-moneda-mala.xlsx", "application/vnd.ms-excel", xlsx);

        mockMvc.perform(multipart(BASE_URL + "/bulk-upload").file(file).header("Authorization", cashierAuthHeader))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("EXPENSE_BULK_VALIDATION_FAILED"));

        mockMvc.perform(get(BASE_URL).param("year", "2026").param("month", "2").header("Authorization", cashierAuthHeader))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(0));
    }

    private byte[] buildBulkExpenseWorkbook(String[][] rows) throws Exception {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Egresos");
            Row header = sheet.createRow(0);
            String[] headers = {"DocumentNumber", "ProviderName", "ExpenseDate", "Amount", "AssociatedDocument", "ExpenseReason", "Moneda"};
            for (int col = 0; col < headers.length; col++) {
                header.createCell(col).setCellValue(headers[col]);
            }
            for (int rowIndex = 0; rowIndex < rows.length; rowIndex++) {
                Row row = sheet.createRow(rowIndex + 1);
                String[] values = rows[rowIndex];
                row.createCell(0).setCellValue(values[0]);
                row.createCell(1).setCellValue(values[1]);
                row.createCell(2).setCellValue(values[2]);
                row.createCell(3).setCellValue(Double.parseDouble(values[3]));
                row.createCell(4).setCellValue(values[4]);
                row.createCell(5).setCellValue(values[5]);
                row.createCell(6).setCellValue(values[6]);
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        }
    }
}
