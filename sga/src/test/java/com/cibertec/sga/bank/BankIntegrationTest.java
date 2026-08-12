package com.cibertec.sga.bank;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cibertec.sga.common.AbstractIntegrationTest;
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
class BankIntegrationTest extends AbstractIntegrationTest {

    private static final String BASE_URL = "/api/banks";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String authHeader;
    private String currencyUuid;

    @BeforeEach
    void loginAsAdminAndFetchCurrency() throws Exception {
        MvcResult login = mockMvc.perform(
            post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content("""
                {"username": "admin", "password": "Admin123!"}
                """)
        ).andReturn();
        String accessToken = objectMapper.readTree(login.getResponse().getContentAsString()).get("accessToken").asText();
        authHeader = "Bearer " + accessToken;

        MvcResult currencies = mockMvc.perform(get("/api/currencies").header("Authorization", authHeader)).andReturn();
        currencyUuid = objectMapper.readTree(currencies.getResponse().getContentAsString()).get(0).get("uuid").asText();
    }

    private String createBank(String accountNumber) throws Exception {
        MvcResult created = mockMvc.perform(
            post(BASE_URL).header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"name": "BCP", "accountNumber": "%s", "cci": "00219400%s", "currencyUuid": "%s"}
                """.formatted(accountNumber, accountNumber, currencyUuid))
        ).andExpect(status().isCreated()).andReturn();
        return objectMapper.readTree(created.getResponse().getContentAsString()).get("uuid").asText();
    }

    @Test
    void createThenAppearsInListAndGetByUuid() throws Exception {
        String uuid = createBank("ACC-001");

        mockMvc.perform(get(BASE_URL + "/{uuid}", uuid).header("Authorization", authHeader))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.currency.uuid").value(currencyUuid))
            .andExpect(jsonPath("$.active").value(true));

        mockMvc.perform(get(BASE_URL).param("search", "ACC-001").header("Authorization", authHeader))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].accountNumber").value("ACC-001"));
    }

    @Test
    void createWithDuplicateAccountNumberReturnsConflict() throws Exception {
        createBank("ACC-002");

        mockMvc.perform(
            post(BASE_URL).header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"name": "Otro Banco", "accountNumber": "ACC-002", "cci": "00219400X", "currencyUuid": "%s"}
                """.formatted(currencyUuid))
        )
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.error").value("BANK_DUPLICATE_ACCOUNT_NUMBER"));
    }

    @Test
    void createWithUnknownCurrencyReturnsBadRequest() throws Exception {
        mockMvc.perform(
            post(BASE_URL).header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"name": "BCP", "accountNumber": "ACC-003", "cci": "00219400Y", "currencyUuid": "%s"}
                """.formatted(UUID.randomUUID()))
        )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("BANK_CURRENCY_NOT_FOUND"));
    }

    @Test
    void deactivateExistingBankFlipsActive() throws Exception {
        String uuid = createBank("ACC-004");

        mockMvc.perform(patch(BASE_URL + "/{uuid}/deactivate", uuid).header("Authorization", authHeader))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.active").value(false));
    }
}
