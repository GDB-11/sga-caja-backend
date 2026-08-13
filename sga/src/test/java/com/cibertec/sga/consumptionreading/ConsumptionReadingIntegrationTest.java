package com.cibertec.sga.consumptionreading;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@AutoConfigureMockMvc
@Transactional
class ConsumptionReadingIntegrationTest extends AbstractIntegrationTest {

    private static final String BASE_URL = "/api/consumption-readings";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String authHeader;
    private String businessTypeUuid;
    private String recurrenceTypeUuid;
    private String stallChargeTargetTypeUuid;
    private String currencyUuid;

    @BeforeEach
    void loginAndCreateReferences() throws Exception {
        MvcResult login = mockMvc.perform(
            post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content("""
                {"username": "admin", "password": "Admin123!"}
                """)
        ).andReturn();
        String accessToken = objectMapper.readTree(login.getResponse().getContentAsString()).get("accessToken").asText();
        authHeader = "Bearer " + accessToken;

        MvcResult businessType = mockMvc.perform(
            post("/api/business-types").header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"name": "Abarrotes CR Test"}
                """)
        ).andExpect(status().isCreated()).andReturn();
        businessTypeUuid = objectMapper.readTree(businessType.getResponse().getContentAsString()).get("uuid").asText();

        MvcResult recurrenceTypes = mockMvc.perform(get("/api/recurrence-types").header("Authorization", authHeader)).andReturn();
        recurrenceTypeUuid = findUuidByName(objectMapper.readTree(recurrenceTypes.getResponse().getContentAsString()), "Monthly");

        MvcResult chargeTargetTypes = mockMvc.perform(get("/api/charge-target-types").header("Authorization", authHeader)).andReturn();
        stallChargeTargetTypeUuid =
            findUuidByName(objectMapper.readTree(chargeTargetTypes.getResponse().getContentAsString()), "Stall");

        MvcResult currencies = mockMvc.perform(get("/api/currencies").header("Authorization", authHeader)).andReturn();
        currencyUuid = objectMapper.readTree(currencies.getResponse().getContentAsString()).get(0).get("uuid").asText();
    }

    private String findUuidByName(JsonNode array, String name) {
        for (JsonNode node : array) {
            if (node.get("name").asText().equals(name)) {
                return node.get("uuid").asText();
            }
        }
        throw new IllegalStateException("No se encontró '" + name + "' en " + array);
    }

    private String createConsumptionService(String name, String unitCost) throws Exception {
        MvcResult created = mockMvc.perform(
            post("/api/services").header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"name": "%s", "recurrenceTypeUuid": "%s", "chargeTargetTypeUuid": "%s", "currencyUuid": "%s",
                 "consumptionBased": true, "unitCost": %s}
                """.formatted(name, recurrenceTypeUuid, stallChargeTargetTypeUuid, currencyUuid, unitCost))
        ).andExpect(status().isCreated()).andReturn();
        return objectMapper.readTree(created.getResponse().getContentAsString()).get("uuid").asText();
    }

    private String createFixedCostService(String name) throws Exception {
        MvcResult created = mockMvc.perform(
            post("/api/services").header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"name": "%s", "recurrenceTypeUuid": "%s", "chargeTargetTypeUuid": "%s", "currencyUuid": "%s",
                 "consumptionBased": false, "cost": 30.00}
                """.formatted(name, recurrenceTypeUuid, stallChargeTargetTypeUuid, currencyUuid))
        ).andExpect(status().isCreated()).andReturn();
        return objectMapper.readTree(created.getResponse().getContentAsString()).get("uuid").asText();
    }

    private void createStall(String number) throws Exception {
        mockMvc.perform(
            post("/api/stalls").header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"number": "%s", "businessTypeUuid": "%s"}
                """.formatted(number, businessTypeUuid))
        ).andExpect(status().isCreated());
    }

    private String generateOneReceivable(String serviceUuid, boolean consumptionBased) throws Exception {
        String amountField = consumptionBased ? "" : ", \"amount\": 30.00";
        MvcResult generated = mockMvc.perform(
            post("/api/account-receivables/generate-by-stall")
                .header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"serviceUuid": "%s", "periodStartDate": "2026-01-01", "periodEndDate": "2026-01-31"%s}
                """.formatted(serviceUuid, amountField))
        ).andExpect(status().isCreated()).andReturn();
        return objectMapper.readTree(generated.getResponse().getContentAsString()).get(0).get("uuid").asText();
    }

    @Test
    void registerReadingCalculatesAmountAndUpdatesAccountReceivable() throws Exception {
        String serviceUuid = createConsumptionService("Agua CR", "4.00");
        createStall("CR-P-001");
        String accountReceivableUuid = generateOneReceivable(serviceUuid, true);

        mockMvc.perform(
            post(BASE_URL).header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"accountReceivableUuid": "%s", "initialReading": 10.00, "finalReading": 35.00}
                """.formatted(accountReceivableUuid))
        )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.unitCost").value(4.00))
            .andExpect(jsonPath("$.calculatedAmount").value(100.00));

        mockMvc.perform(get("/api/account-receivables/{uuid}", accountReceivableUuid).header("Authorization", authHeader))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.amount").value(100.00));
    }

    @Test
    void registerReadingWithLowerFinalReadingFloorsAmountAtZero() throws Exception {
        String serviceUuid = createConsumptionService("Luz CR", "3.00");
        createStall("CR-P-002");
        String accountReceivableUuid = generateOneReceivable(serviceUuid, true);

        mockMvc.perform(
            post(BASE_URL).header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"accountReceivableUuid": "%s", "initialReading": 50.00, "finalReading": 20.00}
                """.formatted(accountReceivableUuid))
        )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.calculatedAmount").value(0.00));
    }

    @Test
    void registerDuplicateReadingReturnsConflict() throws Exception {
        String serviceUuid = createConsumptionService("Gas CR", "2.00");
        createStall("CR-P-003");
        String accountReceivableUuid = generateOneReceivable(serviceUuid, true);

        mockMvc.perform(
            post(BASE_URL).header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"accountReceivableUuid": "%s", "initialReading": 0.00, "finalReading": 10.00}
                """.formatted(accountReceivableUuid))
        ).andExpect(status().isCreated());

        mockMvc.perform(
            post(BASE_URL).header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"accountReceivableUuid": "%s", "initialReading": 10.00, "finalReading": 20.00}
                """.formatted(accountReceivableUuid))
        )
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.error").value("CONSUMPTION_READING_DUPLICATE"));
    }

    @Test
    void registerReadingOnFixedCostServiceReceivableReturnsBadRequest() throws Exception {
        String serviceUuid = createFixedCostService("Mantenimiento CR");
        createStall("CR-P-004");
        String accountReceivableUuid = generateOneReceivable(serviceUuid, false);

        mockMvc.perform(
            post(BASE_URL).header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"accountReceivableUuid": "%s", "initialReading": 0.00, "finalReading": 10.00}
                """.formatted(accountReceivableUuid))
        )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("CONSUMPTION_READING_SERVICE_NOT_CONSUMPTION_BASED"));
    }

    @Test
    void registerReadingWithUnknownAccountReceivableReturnsBadRequest() throws Exception {
        mockMvc.perform(
            post(BASE_URL).header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"accountReceivableUuid": "%s", "initialReading": 0.00, "finalReading": 10.00}
                """.formatted(UUID.randomUUID()))
        )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("CONSUMPTION_READING_ACCOUNT_RECEIVABLE_NOT_FOUND"));
    }
}
