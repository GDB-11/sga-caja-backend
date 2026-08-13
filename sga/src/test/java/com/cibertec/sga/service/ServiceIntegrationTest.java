package com.cibertec.sga.service;

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
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@AutoConfigureMockMvc
@Transactional
class ServiceIntegrationTest extends AbstractIntegrationTest {

    private static final String BASE_URL = "/api/services";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String authHeader;
    private String recurrenceTypeUuid;
    private String stallChargeTargetTypeUuid;
    private String currencyUuid;

    @BeforeEach
    void loginAsAdminAndFetchReferences() throws Exception {
        MvcResult login = mockMvc.perform(
            post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content("""
                {"username": "admin", "password": "Admin123!"}
                """)
        ).andReturn();
        String accessToken = objectMapper.readTree(login.getResponse().getContentAsString()).get("accessToken").asText();
        authHeader = "Bearer " + accessToken;

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

    private String createFixedCostService(String name) throws Exception {
        MvcResult created = mockMvc.perform(
            post(BASE_URL).header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"name": "%s", "recurrenceTypeUuid": "%s", "chargeTargetTypeUuid": "%s", "currencyUuid": "%s",
                 "consumptionBased": false, "cost": 100.00}
                """.formatted(name, recurrenceTypeUuid, stallChargeTargetTypeUuid, currencyUuid))
        ).andExpect(status().isCreated()).andReturn();
        return objectMapper.readTree(created.getResponse().getContentAsString()).get("uuid").asText();
    }

    @Test
    void createFixedCostServiceThenAppearsInListAndGetByUuid() throws Exception {
        String uuid = createFixedCostService("Mantenimiento");

        mockMvc.perform(get(BASE_URL + "/{uuid}", uuid).header("Authorization", authHeader))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.consumptionBased").value(false))
            .andExpect(jsonPath("$.cost").value(100.00))
            .andExpect(jsonPath("$.unitCost").doesNotExist())
            .andExpect(jsonPath("$.chargeTargetType.name").value("Stall"))
            .andExpect(jsonPath("$.active").value(true));

        mockMvc.perform(get(BASE_URL).param("search", "Mantenimiento").header("Authorization", authHeader))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].name").value("Mantenimiento"));
    }

    @Test
    void createConsumptionBasedServiceStoresUnitCostOnly() throws Exception {
        MvcResult created = mockMvc.perform(
            post(BASE_URL).header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"name": "Agua", "recurrenceTypeUuid": "%s", "chargeTargetTypeUuid": "%s", "currencyUuid": "%s",
                 "consumptionBased": true, "unitCost": 5.50}
                """.formatted(recurrenceTypeUuid, stallChargeTargetTypeUuid, currencyUuid))
        ).andExpect(status().isCreated()).andReturn();

        String uuid = objectMapper.readTree(created.getResponse().getContentAsString()).get("uuid").asText();

        mockMvc.perform(get(BASE_URL + "/{uuid}", uuid).header("Authorization", authHeader))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.consumptionBased").value(true))
            .andExpect(jsonPath("$.unitCost").value(5.50))
            .andExpect(jsonPath("$.cost").doesNotExist());
    }

    @Test
    void createFixedCostServiceWithUnitCostInsteadOfCostReturnsBadRequest() throws Exception {
        mockMvc.perform(
            post(BASE_URL).header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"name": "Malo", "recurrenceTypeUuid": "%s", "chargeTargetTypeUuid": "%s", "currencyUuid": "%s",
                 "consumptionBased": false, "unitCost": 5.50}
                """.formatted(recurrenceTypeUuid, stallChargeTargetTypeUuid, currencyUuid))
        )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("SERVICE_INVALID_COST_CONFIGURATION"));
    }

    @Test
    void createWithUnknownCurrencyReturnsBadRequest() throws Exception {
        mockMvc.perform(
            post(BASE_URL).header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"name": "Malo", "recurrenceTypeUuid": "%s", "chargeTargetTypeUuid": "%s", "currencyUuid": "%s",
                 "consumptionBased": false, "cost": 10.00}
                """.formatted(recurrenceTypeUuid, stallChargeTargetTypeUuid, UUID.randomUUID()))
        )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("SERVICE_CURRENCY_NOT_FOUND"));
    }

    @Test
    void deactivateExistingServiceFlipsActive() throws Exception {
        String uuid = createFixedCostService("Vigilancia");

        mockMvc.perform(patch(BASE_URL + "/{uuid}/deactivate", uuid).header("Authorization", authHeader))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.active").value(false));
    }
}
