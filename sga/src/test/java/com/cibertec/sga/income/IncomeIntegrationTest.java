package com.cibertec.sga.income;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cibertec.sga.common.AbstractIntegrationTest;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

@AutoConfigureMockMvc
@Transactional
class IncomeIntegrationTest extends AbstractIntegrationTest {

    private static final String BASE_URL = "/api/incomes";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String authHeader;
    private String cashierAuthHeader;
    private String incomeCategoryUuid;
    private String currencyUuid;

    @BeforeEach
    void loginAndFetchIncomeCategory() throws Exception {
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

        MvcResult categories = mockMvc.perform(get("/api/income-categories").header("Authorization", authHeader)).andReturn();
        incomeCategoryUuid = objectMapper.readTree(categories.getResponse().getContentAsString()).get(0).get("uuid").asString();

        MvcResult currencies = mockMvc.perform(get("/api/currencies").header("Authorization", authHeader)).andReturn();
        currencyUuid = objectMapper.readTree(currencies.getResponse().getContentAsString()).get(0).get("uuid").asString();
    }

    @Test
    void createRegistersIncomeAndEmitsReceipt() throws Exception {
        MvcResult created = mockMvc.perform(
            post(BASE_URL).header("Authorization", cashierAuthHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"depositorName": "Juan Perez", "incomeCategoryUuid": "%s", "currencyUuid": "%s", "concept": "Alquiler de local", "amount": 250.00}
                """.formatted(incomeCategoryUuid, currencyUuid))
        )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.depositorName").value("Juan Perez"))
            .andExpect(jsonPath("$.amount").value(250.00))
            .andExpect(jsonPath("$.currency.uuid").value(currencyUuid))
            .andExpect(jsonPath("$.receipt.receiptTypeName").value("Income"))
            .andExpect(jsonPath("$.receipt.correlativeNumber").isNumber())
            .andReturn();
        String uuid = objectMapper.readTree(created.getResponse().getContentAsString()).get("uuid").asString();

        mockMvc.perform(get(BASE_URL + "/{uuid}", uuid).header("Authorization", cashierAuthHeader))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.concept").value("Alquiler de local"));
    }

    @Test
    void createWithAdministratorRoleReturnsForbidden() throws Exception {
        mockMvc.perform(
            post(BASE_URL).header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"depositorName": "Juan Perez", "incomeCategoryUuid": "%s", "currencyUuid": "%s", "concept": "Donación", "amount": 100.00}
                """.formatted(incomeCategoryUuid, currencyUuid))
        ).andExpect(status().isForbidden());
    }

    @Test
    void createWithUnknownIncomeCategoryReturnsBadRequest() throws Exception {
        mockMvc.perform(
            post(BASE_URL).header("Authorization", cashierAuthHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"depositorName": "Juan Perez", "incomeCategoryUuid": "%s", "currencyUuid": "%s", "concept": "Donación", "amount": 100.00}
                """.formatted(UUID.randomUUID(), currencyUuid))
        )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("INCOME_CATEGORY_NOT_FOUND"));
    }

    @Test
    void createWithUnknownCurrencyReturnsBadRequest() throws Exception {
        mockMvc.perform(
            post(BASE_URL).header("Authorization", cashierAuthHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"depositorName": "Juan Perez", "incomeCategoryUuid": "%s", "currencyUuid": "%s", "concept": "Donación", "amount": 100.00}
                """.formatted(incomeCategoryUuid, UUID.randomUUID()))
        )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("INCOME_CURRENCY_NOT_FOUND"));
    }

    @Test
    void createWithNonPositiveAmountReturnsBadRequest() throws Exception {
        mockMvc.perform(
            post(BASE_URL).header("Authorization", cashierAuthHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"depositorName": "Juan Perez", "incomeCategoryUuid": "%s", "currencyUuid": "%s", "concept": "Donación", "amount": 0}
                """.formatted(incomeCategoryUuid, currencyUuid))
        ).andExpect(status().isBadRequest());
    }

    @Test
    void listReturnsCreatedIncomes() throws Exception {
        mockMvc.perform(
            post(BASE_URL).header("Authorization", cashierAuthHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"depositorName": "Ana Ruiz", "incomeCategoryUuid": "%s", "currencyUuid": "%s", "concept": "Donación anual", "amount": 300.00}
                """.formatted(incomeCategoryUuid, currencyUuid))
        ).andExpect(status().isCreated());

        mockMvc.perform(get(BASE_URL).header("Authorization", cashierAuthHeader))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(1));
    }

    @Test
    void listFiltersByDate() throws Exception {
        mockMvc.perform(
            post(BASE_URL).header("Authorization", cashierAuthHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"depositorName": "Bruno Vidal", "incomeCategoryUuid": "%s", "currencyUuid": "%s", "concept": "Donación", "amount": 40.00}
                """.formatted(incomeCategoryUuid, currencyUuid))
        ).andExpect(status().isCreated());

        mockMvc.perform(get(BASE_URL).param("date", LocalDate.now().toString()).header("Authorization", cashierAuthHeader))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(1))
            .andExpect(jsonPath("$.content[0].depositorName").value("Bruno Vidal"));

        mockMvc.perform(get(BASE_URL).param("date", LocalDate.now().minusDays(1).toString()).header("Authorization", cashierAuthHeader))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(0));
    }
}
