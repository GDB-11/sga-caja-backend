package com.cibertec.sga.payment;

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
class PaymentIntegrationTest extends AbstractIntegrationTest {

    private static final String BASE_URL = "/api/payments";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String authHeader;
    private String cashierAuthHeader;
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
        JsonNode stagesJson = objectMapper.readTree(stages.getResponse().getContentAsString());
        stage1Uuid = findStageUuidByCode(stagesJson, 1);

        MvcResult recurrenceTypes = mockMvc.perform(get("/api/recurrence-types").header("Authorization", authHeader)).andReturn();
        String recurrenceTypeUuid = findUuidByName(objectMapper.readTree(recurrenceTypes.getResponse().getContentAsString()), "Monthly");

        MvcResult chargeTargetTypes = mockMvc.perform(get("/api/charge-target-types").header("Authorization", authHeader)).andReturn();
        JsonNode chargeTargetTypesJson = objectMapper.readTree(chargeTargetTypes.getResponse().getContentAsString());
        String memberChargeTargetTypeUuid = findUuidByName(chargeTargetTypesJson, "Member");
        String stallChargeTargetTypeUuid = findUuidByName(chargeTargetTypesJson, "Stall");

        MvcResult currencies = mockMvc.perform(get("/api/currencies").header("Authorization", authHeader)).andReturn();
        String currencyUuid = objectMapper.readTree(currencies.getResponse().getContentAsString()).get(0).get("uuid").asString();

        MvcResult service = mockMvc.perform(
            post("/api/services").header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"name": "Cuota Socio Pago Test", "recurrenceTypeUuid": "%s", "chargeTargetTypeUuid": "%s", "currencyUuid": "%s",
                 "consumptionBased": false, "cost": 50.00}
                """.formatted(recurrenceTypeUuid, memberChargeTargetTypeUuid, currencyUuid))
        ).andExpect(status().isCreated()).andReturn();
        memberServiceUuid = objectMapper.readTree(service.getResponse().getContentAsString()).get("uuid").asString();

        MvcResult stallService = mockMvc.perform(
            post("/api/services").header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"name": "Mantenimiento Puesto Pago Test", "recurrenceTypeUuid": "%s", "chargeTargetTypeUuid": "%s",
                 "currencyUuid": "%s", "consumptionBased": false, "cost": 30.00}
                """.formatted(recurrenceTypeUuid, stallChargeTargetTypeUuid, currencyUuid))
        ).andExpect(status().isCreated()).andReturn();
        stallServiceUuid = objectMapper.readTree(stallService.getResponse().getContentAsString()).get("uuid").asString();

        MvcResult businessType = mockMvc.perform(
            post("/api/business-types").header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"name": "Abarrotes Pago Test"}
                """)
        ).andExpect(status().isCreated()).andReturn();
        businessTypeUuid = objectMapper.readTree(businessType.getResponse().getContentAsString()).get("uuid").asString();
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

    private String createMemberAndReceivable(String code, String firstName, String lastName) throws Exception {
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
        // generate-by-member targets all active members in the given stages, so filter by full name
        // to isolate the receivable belonging to the member this test just created.
        JsonNode created = objectMapper.readTree(generated.getResponse().getContentAsString());
        for (JsonNode node : created) {
            String fullName = node.get("member").get("fullName").asString();
            if (fullName.equals(firstName + " " + lastName)) {
                return node.get("uuid").asString();
            }
        }
        throw new IllegalStateException("No se encontró la cuenta por cobrar generada para " + firstName + " " + lastName);
    }

    private String createStallAndReceivable(String number) throws Exception {
        mockMvc.perform(
            post("/api/stalls").header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"number": "%s", "businessTypeUuid": "%s"}
                """.formatted(number, businessTypeUuid))
        ).andExpect(status().isCreated());

        MvcResult generated = mockMvc.perform(
            post("/api/account-receivables/generate-by-stall")
                .header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"serviceUuid": "%s", "periodStartDate": "2026-01-01", "periodEndDate": "2026-01-31", "amount": 30.00}
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
    void computeTotalSumsSelectedPendingReceivables() throws Exception {
        String receivableUuid = createMemberAndReceivable("PAY-001", "Elena", "Torres");

        mockMvc.perform(
            post(BASE_URL + "/compute-total").header("Authorization", cashierAuthHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"accountReceivableUuids": ["%s"]}
                """.formatted(receivableUuid))
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.total").value(50.00))
            .andExpect(jsonPath("$.items.length()").value(1));
    }

    @Test
    void processPaymentEmitsReceiptAndMarksReceivablePaid() throws Exception {
        String receivableUuid = createMemberAndReceivable("PAY-002", "Marco", "Diaz");

        mockMvc.perform(
            post(BASE_URL).header("Authorization", cashierAuthHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"accountReceivableUuids": ["%s"]}
                """.formatted(receivableUuid))
        )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.totalAmount").value(50.00))
            .andExpect(jsonPath("$.receipt.receiptTypeName").value("Income"))
            .andExpect(jsonPath("$.receipt.correlativeNumber").isNumber())
            .andExpect(jsonPath("$.details.length()").value(1));

        mockMvc.perform(get("/api/account-receivables/{uuid}", receivableUuid).header("Authorization", authHeader))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status.name").value("Paid"));
    }

    @Test
    void processPaymentWithAdministratorRoleReturnsForbidden() throws Exception {
        String receivableUuid = createMemberAndReceivable("PAY-003", "Lucia", "Rojas");

        mockMvc.perform(
            post(BASE_URL).header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"accountReceivableUuids": ["%s"]}
                """.formatted(receivableUuid))
        ).andExpect(status().isForbidden());
    }

    @Test
    void processPaymentOnAlreadyPaidReceivableReturnsConflict() throws Exception {
        String receivableUuid = createMemberAndReceivable("PAY-004", "Nora", "Salas");

        mockMvc.perform(
            post(BASE_URL).header("Authorization", cashierAuthHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"accountReceivableUuids": ["%s"]}
                """.formatted(receivableUuid))
        ).andExpect(status().isCreated());

        mockMvc.perform(
            post(BASE_URL).header("Authorization", cashierAuthHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"accountReceivableUuids": ["%s"]}
                """.formatted(receivableUuid))
        )
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.error").value("PAYMENT_ACCOUNT_RECEIVABLE_NOT_PENDING"));
    }

    @Test
    void processPaymentWithUnknownReceivableReturnsBadRequest() throws Exception {
        mockMvc.perform(
            post(BASE_URL).header("Authorization", cashierAuthHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"accountReceivableUuids": ["%s"]}
                """.formatted(UUID.randomUUID()))
        )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("PAYMENT_ACCOUNT_RECEIVABLE_NOT_FOUND"));
    }

    @Test
    void processPaymentWithEmptySelectionReturnsBadRequest() throws Exception {
        mockMvc.perform(
            post(BASE_URL).header("Authorization", cashierAuthHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"accountReceivableUuids": []}
                """)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void computeTotalSumsMultipleSelectedPendingReceivables() throws Exception {
        String receivable1 = createMemberAndReceivable("PAY-005", "Hugo", "Paredes");
        String receivable2 = createMemberAndReceivable("PAY-006", "Vera", "Campos");

        mockMvc.perform(
            post(BASE_URL + "/compute-total").header("Authorization", cashierAuthHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"accountReceivableUuids": ["%s", "%s"]}
                """.formatted(receivable1, receivable2))
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.total").value(100.00))
            .andExpect(jsonPath("$.items.length()").value(2));
    }

    @Test
    void processPaymentWithMultipleReceivablesEmitsSingleReceiptCoveringTotal() throws Exception {
        String receivable1 = createMemberAndReceivable("PAY-007", "Ines", "Delgado");
        String receivable2 = createMemberAndReceivable("PAY-008", "Raul", "Ibarra");

        mockMvc.perform(
            post(BASE_URL).header("Authorization", cashierAuthHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"accountReceivableUuids": ["%s", "%s"]}
                """.formatted(receivable1, receivable2))
        )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.totalAmount").value(100.00))
            .andExpect(jsonPath("$.receipt.correlativeNumber").isNumber())
            .andExpect(jsonPath("$.details.length()").value(2));

        mockMvc.perform(get("/api/account-receivables/{uuid}", receivable1).header("Authorization", authHeader))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status.name").value("Paid"));
        mockMvc.perform(get("/api/account-receivables/{uuid}", receivable2).header("Authorization", authHeader))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status.name").value("Paid"));
    }

    /**
     * RNF-04: si una de las cuentas seleccionadas ya no está pendiente, la operación completa
     * debe rechazarse — no debe emitir un recibo parcial ni dejar la cuenta pendiente restante
     * marcada como pagada sin comprobante. {@code PaymentService.validateSelectionForUpdate}
     * valida la lista completa antes de escribir nada, así que esta prueba fija ese
     * comportamiento como contrato, no solo como detalle de implementación.
     */
    @Test
    void processPaymentWithPartiallyPaidSelectionFailsAndLeavesPendingReceivableUntouched() throws Exception {
        String alreadyPaidReceivable = createMemberAndReceivable("PAY-009", "Teo", "Cabrera");
        mockMvc.perform(
            post(BASE_URL).header("Authorization", cashierAuthHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"accountReceivableUuids": ["%s"]}
                """.formatted(alreadyPaidReceivable))
        ).andExpect(status().isCreated());

        String stillPendingReceivable = createMemberAndReceivable("PAY-010", "Gaby", "Montes");

        mockMvc.perform(
            post(BASE_URL).header("Authorization", cashierAuthHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"accountReceivableUuids": ["%s", "%s"]}
                """.formatted(alreadyPaidReceivable, stillPendingReceivable))
        )
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.error").value("PAYMENT_ACCOUNT_RECEIVABLE_NOT_PENDING"));

        mockMvc.perform(get("/api/account-receivables/{uuid}", stillPendingReceivable).header("Authorization", authHeader))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status.name").value("Pending"));
    }

    @Test
    void processPaymentForStallReceivableEmitsReceiptAndMarksReceivablePaid() throws Exception {
        String receivableUuid = createStallAndReceivable("PAY-P-001");

        mockMvc.perform(
            post(BASE_URL).header("Authorization", cashierAuthHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"accountReceivableUuids": ["%s"]}
                """.formatted(receivableUuid))
        )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.totalAmount").value(30.00))
            .andExpect(jsonPath("$.receipt.receiptTypeName").value("Income"))
            .andExpect(jsonPath("$.receipt.correlativeNumber").isNumber())
            .andExpect(jsonPath("$.details.length()").value(1));

        mockMvc.perform(get("/api/account-receivables/{uuid}", receivableUuid).header("Authorization", authHeader))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status.name").value("Paid"));
    }

    @Test
    void processPaymentRecordsActingUserForAudit() throws Exception {
        String receivableUuid = createMemberAndReceivable("PAY-011", "Wendy", "Alva");

        mockMvc.perform(
            post(BASE_URL).header("Authorization", cashierAuthHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"accountReceivableUuids": ["%s"]}
                """.formatted(receivableUuid))
        )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.createdBy.username").value("cashier"))
            .andExpect(jsonPath("$.createdBy.uuid").isNotEmpty());
    }
}
