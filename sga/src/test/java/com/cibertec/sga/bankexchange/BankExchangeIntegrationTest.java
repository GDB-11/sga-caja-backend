package com.cibertec.sga.bankexchange;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cibertec.sga.common.AbstractIntegrationTest;
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
class BankExchangeIntegrationTest extends AbstractIntegrationTest {

    private static final String BASE_URL = "/api/bank-exchanges";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String authHeader;
    private String cashierAuthHeader;
    private String bankUuid;
    private String secondCurrencyBankUuid;
    private String memberServiceUuid;
    private String stallServiceUuid;
    private String businessTypeUuid;
    private String stage1Uuid;

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
        JsonNode currenciesJson = objectMapper.readTree(currencies.getResponse().getContentAsString());
        String currencyUuid = currenciesJson.get(0).get("uuid").asString();
        String secondCurrencyUuid = currenciesJson.get(1).get("uuid").asString();

        memberServiceUuid = createFixedCostService("Cuota Socio Canje Test", recurrenceTypeUuid, memberChargeTargetTypeUuid, currencyUuid);
        stallServiceUuid = createFixedCostService("Mantenimiento Canje Test", recurrenceTypeUuid, stallChargeTargetTypeUuid, currencyUuid);

        MvcResult businessType = mockMvc.perform(
            post("/api/business-types").header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"name": "Abarrotes Canje Test"}
                """)
        ).andExpect(status().isCreated()).andReturn();
        businessTypeUuid = objectMapper.readTree(businessType.getResponse().getContentAsString()).get("uuid").asString();

        MvcResult bank = mockMvc.perform(
            post("/api/banks").header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"name": "BCP", "accountNumber": "BX-ACC-001", "cci": "00219400BX1", "currencyUuid": "%s"}
                """.formatted(currencyUuid))
        ).andExpect(status().isCreated()).andReturn();
        bankUuid = objectMapper.readTree(bank.getResponse().getContentAsString()).get("uuid").asString();

        MvcResult secondCurrencyBank = mockMvc.perform(
            post("/api/banks").header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"name": "Interbank USD", "accountNumber": "BX-ACC-002", "cci": "00319400BX2", "currencyUuid": "%s"}
                """.formatted(secondCurrencyUuid))
        ).andExpect(status().isCreated()).andReturn();
        secondCurrencyBankUuid = objectMapper.readTree(secondCurrencyBank.getResponse().getContentAsString()).get("uuid").asString();
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
        String name, String recurrenceTypeUuid, String chargeTargetTypeUuid, String currencyUuid
    ) throws Exception {
        MvcResult created = mockMvc.perform(
            post("/api/services").header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"name": "%s", "recurrenceTypeUuid": "%s", "chargeTargetTypeUuid": "%s", "currencyUuid": "%s",
                 "consumptionBased": false, "cost": 40.00}
                """.formatted(name, recurrenceTypeUuid, chargeTargetTypeUuid, currencyUuid))
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
                {"serviceUuid": "%s", "periodStartDate": "2026-01-01", "periodEndDate": "2026-01-31", "amount": 40.00,
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

    private String createStallReceivable(String number) throws Exception {
        mockMvc.perform(
            post("/api/stalls").header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"number": "%s", "businessTypeUuid": "%s"}
                """.formatted(number, businessTypeUuid))
        ).andExpect(status().isCreated());

        MvcResult generated = mockMvc.perform(
            post("/api/account-receivables/generate-by-stall")
                .header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"serviceUuid": "%s", "periodStartDate": "2026-01-01", "periodEndDate": "2026-01-31", "amount": 40.00}
                """.formatted(stallServiceUuid))
        ).andExpect(status().isCreated()).andReturn();
        JsonNode created = objectMapper.readTree(generated.getResponse().getContentAsString());
        for (JsonNode node : created) {
            if (node.get("stall").get("number").asString().equals(number)) {
                return node.get("uuid").asString();
            }
        }
        throw new IllegalStateException("No se encontró la cuenta por cobrar generada para el puesto " + number);
    }

    @Test
    void createExchangesMemberReceivableAndMarksItPaid() throws Exception {
        String receivableUuid = createMemberReceivable("BX-001", "Pedro", "Luna");

        mockMvc.perform(
            post(BASE_URL).header("Authorization", cashierAuthHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"accountReceivableUuid": "%s", "bankUuid": "%s", "depositDate": "2026-02-10"}
                """.formatted(receivableUuid, bankUuid))
        )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.amount").value(40.00))
            .andExpect(jsonPath("$.receipt.receiptTypeName").value("BankTransaction"))
            .andExpect(jsonPath("$.bank.uuid").value(bankUuid));

        mockMvc.perform(get("/api/account-receivables/{uuid}", receivableUuid).header("Authorization", authHeader))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status.name").value("Paid"));
    }

    @Test
    void createWithStallReceivableReturnsBadRequest() throws Exception {
        String receivableUuid = createStallReceivable("BX-P-001");

        mockMvc.perform(
            post(BASE_URL).header("Authorization", cashierAuthHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"accountReceivableUuid": "%s", "bankUuid": "%s", "depositDate": "2026-02-10"}
                """.formatted(receivableUuid, bankUuid))
        )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("BANK_EXCHANGE_ACCOUNT_RECEIVABLE_NOT_MEMBER_TARGET"));
    }

    @Test
    void createWithAdministratorRoleReturnsForbidden() throws Exception {
        String receivableUuid = createMemberReceivable("BX-002", "Carla", "Mora");

        mockMvc.perform(
            post(BASE_URL).header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"accountReceivableUuid": "%s", "bankUuid": "%s", "depositDate": "2026-02-10"}
                """.formatted(receivableUuid, bankUuid))
        ).andExpect(status().isForbidden());
    }

    @Test
    void createOnAlreadyPaidReceivableReturnsConflict() throws Exception {
        String receivableUuid = createMemberReceivable("BX-003", "Hugo", "Paz");

        mockMvc.perform(
            post(BASE_URL).header("Authorization", cashierAuthHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"accountReceivableUuid": "%s", "bankUuid": "%s", "depositDate": "2026-02-10"}
                """.formatted(receivableUuid, bankUuid))
        ).andExpect(status().isCreated());

        mockMvc.perform(
            post(BASE_URL).header("Authorization", cashierAuthHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"accountReceivableUuid": "%s", "bankUuid": "%s", "depositDate": "2026-02-11"}
                """.formatted(receivableUuid, bankUuid))
        )
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.error").value("BANK_EXCHANGE_ACCOUNT_RECEIVABLE_NOT_PENDING"));
    }

    @Test
    void createWithCurrencyMismatchBetweenReceivableAndBankReturnsBadRequest() throws Exception {
        String receivableUuid = createMemberReceivable("BX-004", "Elsa", "Vega");

        mockMvc.perform(
            post(BASE_URL).header("Authorization", cashierAuthHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"accountReceivableUuid": "%s", "bankUuid": "%s", "depositDate": "2026-02-10"}
                """.formatted(receivableUuid, secondCurrencyBankUuid))
        )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("BANK_EXCHANGE_CURRENCY_MISMATCH"));

        mockMvc.perform(get("/api/account-receivables/{uuid}", receivableUuid).header("Authorization", authHeader))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status.name").value("Pending"));
    }
}
