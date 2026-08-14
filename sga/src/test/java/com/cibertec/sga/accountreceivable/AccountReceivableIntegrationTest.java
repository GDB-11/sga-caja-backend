package com.cibertec.sga.accountreceivable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cibertec.sga.common.AbstractIntegrationTest;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
class AccountReceivableIntegrationTest extends AbstractIntegrationTest {

    private static final String BASE_URL = "/api/account-receivables";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String authHeader;
    private String cashierAuthHeader;
    private String businessTypeUuid;
    private String stage1Uuid;
    private String stage2Uuid;
    private String stallServiceUuid;
    private String memberServiceUuid;

    @BeforeEach
    void loginAndCreateReferences() throws Exception {
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
        String cashierAccessToken = objectMapper.readTree(cashierLogin.getResponse().getContentAsString()).get("accessToken").asString();
        cashierAuthHeader = "Bearer " + cashierAccessToken;

        MvcResult businessType = mockMvc.perform(
            post("/api/business-types").header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"name": "Abarrotes AR Test"}
                """)
        ).andExpect(status().isCreated()).andReturn();
        businessTypeUuid = objectMapper.readTree(businessType.getResponse().getContentAsString()).get("uuid").asString();

        MvcResult stages = mockMvc.perform(get("/api/stages").header("Authorization", authHeader)).andReturn();
        JsonNode stagesJson = objectMapper.readTree(stages.getResponse().getContentAsString());
        stage1Uuid = findStageUuidByCode(stagesJson, 1);
        stage2Uuid = findStageUuidByCode(stagesJson, 2);

        MvcResult recurrenceTypes = mockMvc.perform(get("/api/recurrence-types").header("Authorization", authHeader)).andReturn();
        String recurrenceTypeUuid = findUuidByName(objectMapper.readTree(recurrenceTypes.getResponse().getContentAsString()), "Monthly");

        MvcResult chargeTargetTypes = mockMvc.perform(get("/api/charge-target-types").header("Authorization", authHeader)).andReturn();
        JsonNode chargeTargetTypesJson = objectMapper.readTree(chargeTargetTypes.getResponse().getContentAsString());
        String stallChargeTargetTypeUuid = findUuidByName(chargeTargetTypesJson, "Stall");
        String memberChargeTargetTypeUuid = findUuidByName(chargeTargetTypesJson, "Member");

        MvcResult currencies = mockMvc.perform(get("/api/currencies").header("Authorization", authHeader)).andReturn();
        String currencyUuid = objectMapper.readTree(currencies.getResponse().getContentAsString()).get(0).get("uuid").asString();

        stallServiceUuid = createFixedCostService("Mantenimiento Puesto", recurrenceTypeUuid, stallChargeTargetTypeUuid, currencyUuid);
        memberServiceUuid = createFixedCostService("Cuota Socio", recurrenceTypeUuid, memberChargeTargetTypeUuid, currencyUuid);
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
                 "consumptionBased": false, "cost": 30.00}
                """.formatted(name, recurrenceTypeUuid, chargeTargetTypeUuid, currencyUuid))
        ).andExpect(status().isCreated()).andReturn();
        return objectMapper.readTree(created.getResponse().getContentAsString()).get("uuid").asString();
    }

    private void createStall(String number) throws Exception {
        mockMvc.perform(
            post("/api/stalls").header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"number": "%s", "businessTypeUuid": "%s"}
                """.formatted(number, businessTypeUuid))
        ).andExpect(status().isCreated());
    }

    private String createMember(String code, String firstName, String lastName, String stageUuid) throws Exception {
        MvcResult created = mockMvc.perform(
            post("/api/members").header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"code": "%s", "firstName": "%s", "lastName": "%s", "stageUuid": "%s"}
                """.formatted(code, firstName, lastName, stageUuid))
        ).andExpect(status().isCreated()).andReturn();
        return objectMapper.readTree(created.getResponse().getContentAsString()).get("uuid").asString();
    }

    @Test
    void generateByStallCreatesOneAccountReceivablePerActiveStall() throws Exception {
        createStall("AR-P-001");
        createStall("AR-P-002");

        mockMvc.perform(
            post(BASE_URL + "/generate-by-stall").header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"serviceUuid": "%s", "periodStartDate": "2026-01-01", "periodEndDate": "2026-01-31", "amount": 30.00}
                """.formatted(stallServiceUuid))
        )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].amount").value(30.00))
            .andExpect(jsonPath("$[0].stall").exists())
            .andExpect(jsonPath("$[0].member").doesNotExist())
            .andExpect(jsonPath("$[0].status.name").value("Pending"));
    }

    @Test
    void generateByStallWithMemberTargetedServiceReturnsBadRequest() throws Exception {
        mockMvc.perform(
            post(BASE_URL + "/generate-by-stall").header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"serviceUuid": "%s", "periodStartDate": "2026-01-01", "periodEndDate": "2026-01-31", "amount": 30.00}
                """.formatted(memberServiceUuid))
        )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("ACCOUNT_RECEIVABLE_WRONG_CHARGE_TARGET"));
    }

    @Test
    void generateByStallWithInvalidPeriodReturnsBadRequest() throws Exception {
        mockMvc.perform(
            post(BASE_URL + "/generate-by-stall").header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"serviceUuid": "%s", "periodStartDate": "2026-02-01", "periodEndDate": "2026-01-01", "amount": 30.00}
                """.formatted(stallServiceUuid))
        )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("ACCOUNT_RECEIVABLE_INVALID_PERIOD"));
    }

    @Test
    void generateByStallWithoutAmountReturnsBadRequest() throws Exception {
        mockMvc.perform(
            post(BASE_URL + "/generate-by-stall").header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"serviceUuid": "%s", "periodStartDate": "2026-01-01", "periodEndDate": "2026-01-31"}
                """.formatted(stallServiceUuid))
        )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("ACCOUNT_RECEIVABLE_INVALID_AMOUNT"));
    }

    @Test
    void generateByMemberWithUniqueMembersDedupesBySameFullName() throws Exception {
        // stageCodes [1, 2] matches every active member in those stages across the shared test
        // database (see AbstractIntegrationTest's singleton container), not just the ones created
        // here, so assertions below are scoped to this test's own member uuids instead of raw
        // response length.
        String member1Uuid = createMember("AR-M-001", "Ana", "Ruiz", stage1Uuid);
        String member2Uuid = createMember("AR-M-002", "Ana", "Ruiz", stage2Uuid);
        String member3Uuid = createMember("AR-M-003", "Juan", "Perez", stage1Uuid);
        Set<String> createdMemberUuids = Set.of(member1Uuid, member2Uuid, member3Uuid);

        MvcResult withoutDedup = mockMvc.perform(
            post(BASE_URL + "/generate-by-member").header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"serviceUuid": "%s", "periodStartDate": "2026-01-01", "periodEndDate": "2026-01-31", "amount": 30.00,
                 "stageCodes": [1, 2], "uniqueMembers": false}
                """.formatted(memberServiceUuid))
        ).andExpect(status().isCreated()).andReturn();
        assertThat(memberFullNamesFor(withoutDedup, createdMemberUuids)).containsExactlyInAnyOrder("Ana Ruiz", "Ana Ruiz", "Juan Perez");

        MvcResult withDedup = mockMvc.perform(
            post(BASE_URL + "/generate-by-member").header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"serviceUuid": "%s", "periodStartDate": "2026-02-01", "periodEndDate": "2026-02-28", "amount": 30.00,
                 "stageCodes": [1, 2], "uniqueMembers": true}
                """.formatted(memberServiceUuid))
        ).andExpect(status().isCreated()).andReturn();
        assertThat(memberFullNamesFor(withDedup, createdMemberUuids)).containsExactlyInAnyOrder("Ana Ruiz", "Juan Perez");
    }

    private List<String> memberFullNamesFor(MvcResult result, Set<String> memberUuids) throws Exception {
        JsonNode receivables = objectMapper.readTree(result.getResponse().getContentAsString());
        List<String> fullNames = new ArrayList<>();
        for (JsonNode receivable : receivables) {
            String memberUuid = receivable.get("member").get("uuid").asString();
            if (memberUuids.contains(memberUuid)) {
                fullNames.add(receivable.get("member").get("fullName").asString());
            }
        }
        return fullNames;
    }

    @Test
    void generateByMemberWithoutStageCodesReturnsBadRequest() throws Exception {
        mockMvc.perform(
            post(BASE_URL + "/generate-by-member").header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"serviceUuid": "%s", "periodStartDate": "2026-01-01", "periodEndDate": "2026-01-31", "amount": 30.00,
                 "stageCodes": [], "uniqueMembers": false}
                """.formatted(memberServiceUuid))
        ).andExpect(status().isBadRequest());
    }

    @Test
    void markExemptFlipsPendingReceivableToExempt() throws Exception {
        createStall("AR-EX-001");
        String accountReceivableUuid = generateOneStallReceivable();

        mockMvc.perform(patch(BASE_URL + "/{uuid}/exempt", accountReceivableUuid).header("Authorization", cashierAuthHeader))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status.name").value("Exempt"));
    }

    @Test
    void markExemptWithAdministratorRoleReturnsForbidden() throws Exception {
        createStall("AR-EX-002");
        String accountReceivableUuid = generateOneStallReceivable();

        mockMvc.perform(patch(BASE_URL + "/{uuid}/exempt", accountReceivableUuid).header("Authorization", authHeader))
            .andExpect(status().isForbidden());
    }

    @Test
    void markExemptOnAlreadyExemptReceivableReturnsConflict() throws Exception {
        createStall("AR-EX-003");
        String accountReceivableUuid = generateOneStallReceivable();

        mockMvc.perform(patch(BASE_URL + "/{uuid}/exempt", accountReceivableUuid).header("Authorization", cashierAuthHeader))
            .andExpect(status().isOk());

        mockMvc.perform(patch(BASE_URL + "/{uuid}/exempt", accountReceivableUuid).header("Authorization", cashierAuthHeader))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.error").value("ACCOUNT_RECEIVABLE_NOT_PENDING"));
    }

    @Test
    void summaryByMemberReturnsReceivablesWithoutMovementWhilePending() throws Exception {
        createMember("AR-SUM-001", "Rosa", "Vega", stage1Uuid);
        MvcResult generated = mockMvc.perform(
            post(BASE_URL + "/generate-by-member").header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"serviceUuid": "%s", "periodStartDate": "2026-01-01", "periodEndDate": "2026-01-31", "amount": 30.00,
                 "stageCodes": [1], "uniqueMembers": false}
                """.formatted(memberServiceUuid))
        ).andExpect(status().isCreated()).andReturn();
        String memberUuid = objectMapper.readTree(generated.getResponse().getContentAsString())
            .get(0).get("member").get("uuid").asString();

        mockMvc.perform(get(BASE_URL + "/summary").param("memberUuid", memberUuid).header("Authorization", authHeader))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].accountReceivable.status.name").value("Pending"))
            .andExpect(jsonPath("$[0].settlementMethod").doesNotExist());
    }

    @Test
    void summaryWithBothMemberAndStallReturnsBadRequest() throws Exception {
        mockMvc.perform(
            get(BASE_URL + "/summary")
                .param("memberUuid", UUID.randomUUID().toString())
                .param("stallUuid", UUID.randomUUID().toString())
                .header("Authorization", authHeader)
        )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("ACCOUNT_RECEIVABLE_INVALID_SUMMARY_TARGET"));
    }

    @Test
    void summaryWithUnknownMemberReturnsNotFound() throws Exception {
        mockMvc.perform(get(BASE_URL + "/summary").param("memberUuid", UUID.randomUUID().toString()).header("Authorization", authHeader))
            .andExpect(status().isNotFound());
    }

    private String generateOneStallReceivable() throws Exception {
        MvcResult generated = mockMvc.perform(
            post(BASE_URL + "/generate-by-stall").header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"serviceUuid": "%s", "periodStartDate": "2026-01-01", "periodEndDate": "2026-01-31", "amount": 30.00}
                """.formatted(stallServiceUuid))
        ).andExpect(status().isCreated()).andReturn();
        return objectMapper.readTree(generated.getResponse().getContentAsString()).get(0).get("uuid").asString();
    }
}
