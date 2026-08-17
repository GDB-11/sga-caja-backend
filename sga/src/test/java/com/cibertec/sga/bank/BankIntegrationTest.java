package com.cibertec.sga.bank;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
    private String cashierAuthHeader;
    private String currencyUuid;

    @BeforeEach
    void loginAsAdminAndFetchCurrency() throws Exception {
        MvcResult login = mockMvc.perform(
            post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content("""
                {"username": "admin", "password": "Admin123!"}
                """)
        ).andReturn();
        String accessToken = objectMapper.readTree(login.getResponse().getContentAsString()).get("accessToken").asString();
        authHeader = "Bearer " + accessToken;

        MvcResult cashierLogin = mockMvc.perform(
            post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content("""
                {"username": "cashier", "password": "Cashier123!"}
                """)
        ).andReturn();
        cashierAuthHeader = "Bearer " + objectMapper.readTree(cashierLogin.getResponse().getContentAsString()).get("accessToken").asString();

        MvcResult currencies = mockMvc.perform(get("/api/currencies").header("Authorization", authHeader)).andReturn();
        currencyUuid = objectMapper.readTree(currencies.getResponse().getContentAsString()).get(0).get("uuid").asString();
    }

    private String createBank(String accountNumber) throws Exception {
        MvcResult created = mockMvc.perform(
            post(BASE_URL).header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"name": "BCP", "accountNumber": "%s", "cci": "00219400%s", "currencyUuid": "%s"}
                """.formatted(accountNumber, accountNumber, currencyUuid))
        ).andExpect(status().isCreated()).andReturn();
        return objectMapper.readTree(created.getResponse().getContentAsString()).get("uuid").asString();
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

    @Test
    void findByUnknownUuidReturnsNotFound() throws Exception {
        mockMvc.perform(get(BASE_URL + "/{uuid}", UUID.randomUUID()).header("Authorization", authHeader))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value("BANK_NOT_FOUND"));
    }

    @Test
    void updateExistingBankChangesFields() throws Exception {
        String uuid = createBank("ACC-005");

        mockMvc.perform(
            put(BASE_URL + "/{uuid}", uuid).header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"name": "Interbank", "accountNumber": "ACC-005-EDIT", "cci": "00300400ACC005EDIT", "currencyUuid": "%s"}
                """.formatted(currencyUuid))
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Interbank"))
            .andExpect(jsonPath("$.accountNumber").value("ACC-005-EDIT"));
    }

    @Test
    void updateNonExistentUuidReturnsNotFound() throws Exception {
        mockMvc.perform(
            put(BASE_URL + "/{uuid}", UUID.randomUUID())
                .header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"name": "No existe", "accountNumber": "ACC-006", "cci": "00300400ACC006", "currencyUuid": "%s"}
                """.formatted(currencyUuid))
        )
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value("BANK_NOT_FOUND"));
    }

    @Test
    void updateWithDuplicateAccountNumberReturnsConflict() throws Exception {
        createBank("ACC-007");
        String otherUuid = createBank("ACC-008");

        mockMvc.perform(
            put(BASE_URL + "/{uuid}", otherUuid)
                .header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"name": "BCP", "accountNumber": "ACC-007", "cci": "00300400ACC008", "currencyUuid": "%s"}
                """.formatted(currencyUuid))
        )
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.error").value("BANK_DUPLICATE_ACCOUNT_NUMBER"));
    }

    @Test
    void updateWithUnknownCurrencyReturnsBadRequest() throws Exception {
        String uuid = createBank("ACC-009");

        mockMvc.perform(
            put(BASE_URL + "/{uuid}", uuid).header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"name": "BCP", "accountNumber": "ACC-009", "cci": "00300400ACC009", "currencyUuid": "%s"}
                """.formatted(UUID.randomUUID()))
        )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("BANK_CURRENCY_NOT_FOUND"));
    }

    @Test
    void deactivateNonExistentUuidReturnsNotFound() throws Exception {
        mockMvc.perform(patch(BASE_URL + "/{uuid}/deactivate", UUID.randomUUID()).header("Authorization", authHeader))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value("BANK_NOT_FOUND"));
    }

    @Test
    void createWithCashierRoleReturnsForbidden() throws Exception {
        mockMvc.perform(
            post(BASE_URL).header("Authorization", cashierAuthHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"name": "BCP", "accountNumber": "ACC-010", "cci": "00300400ACC010", "currencyUuid": "%s"}
                """.formatted(currencyUuid))
        ).andExpect(status().isForbidden());
    }

    @Test
    void updateWithCashierRoleReturnsForbidden() throws Exception {
        String uuid = createBank("ACC-011");

        mockMvc.perform(
            put(BASE_URL + "/{uuid}", uuid)
                .header("Authorization", cashierAuthHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"name": "BCP", "accountNumber": "ACC-011", "cci": "00300400ACC011", "currencyUuid": "%s"}
                """.formatted(currencyUuid))
        ).andExpect(status().isForbidden());
    }

    @Test
    void deactivateWithCashierRoleReturnsForbidden() throws Exception {
        String uuid = createBank("ACC-012");

        mockMvc.perform(patch(BASE_URL + "/{uuid}/deactivate", uuid).header("Authorization", cashierAuthHeader))
            .andExpect(status().isForbidden());
    }

    @Test
    void createWithBlankNameReturnsBadRequest() throws Exception {
        mockMvc.perform(
            post(BASE_URL).header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"name": "", "accountNumber": "ACC-013", "cci": "00300400ACC013", "currencyUuid": "%s"}
                """.formatted(currencyUuid))
        ).andExpect(status().isBadRequest());
    }

    @Test
    void createWithoutCurrencyUuidReturnsBadRequest() throws Exception {
        mockMvc.perform(
            post(BASE_URL).header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"name": "BCP", "accountNumber": "ACC-014", "cci": "00300400ACC014"}
                """)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void searchFiltersByActiveFlag() throws Exception {
        String activeUuid = createBank("ACC-015");
        String inactiveUuid = createBank("ACC-016");
        mockMvc.perform(patch(BASE_URL + "/{uuid}/deactivate", inactiveUuid).header("Authorization", authHeader))
            .andExpect(status().isOk());

        mockMvc.perform(get(BASE_URL).param("active", "true").param("search", "ACC-01").header("Authorization", authHeader))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[*].uuid", org.hamcrest.Matchers.hasItem(activeUuid)))
            .andExpect(jsonPath("$.content[*].uuid", org.hamcrest.Matchers.not(org.hamcrest.Matchers.hasItem(inactiveUuid))));

        mockMvc.perform(get(BASE_URL).param("active", "false").param("search", "ACC-01").header("Authorization", authHeader))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[*].uuid", org.hamcrest.Matchers.hasItem(inactiveUuid)))
            .andExpect(jsonPath("$.content[*].uuid", org.hamcrest.Matchers.not(org.hamcrest.Matchers.hasItem(activeUuid))));
    }

    @Test
    void searchWithNoMatchesReturnsEmptyPage() throws Exception {
        mockMvc.perform(get(BASE_URL).param("search", "NO-SUCH-BANK-XYZ").header("Authorization", authHeader))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(0));
    }

    @Test
    void deactivateAlreadyInactiveBankStaysInactive() throws Exception {
        String uuid = createBank("ACC-017");
        mockMvc.perform(patch(BASE_URL + "/{uuid}/deactivate", uuid).header("Authorization", authHeader))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.active").value(false));

        mockMvc.perform(patch(BASE_URL + "/{uuid}/deactivate", uuid).header("Authorization", authHeader))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    void activateExistingBankFlipsActive() throws Exception {
        String uuid = createBank("ACC-018");
        mockMvc.perform(patch(BASE_URL + "/{uuid}/deactivate", uuid).header("Authorization", authHeader))
            .andExpect(status().isOk());

        mockMvc.perform(patch(BASE_URL + "/{uuid}/activate", uuid).header("Authorization", authHeader))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void activateNonExistentUuidReturnsNotFound() throws Exception {
        mockMvc.perform(patch(BASE_URL + "/{uuid}/activate", UUID.randomUUID()).header("Authorization", authHeader))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value("BANK_NOT_FOUND"));
    }

    @Test
    void activateWithCashierRoleReturnsForbidden() throws Exception {
        String uuid = createBank("ACC-019");

        mockMvc.perform(patch(BASE_URL + "/{uuid}/activate", uuid).header("Authorization", cashierAuthHeader))
            .andExpect(status().isForbidden());
    }
}
