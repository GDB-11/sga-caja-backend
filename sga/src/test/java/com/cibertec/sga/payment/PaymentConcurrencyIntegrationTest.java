package com.cibertec.sga.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cibertec.sga.common.AbstractIntegrationTest;
import com.cibertec.sga.common.result.Result;
import com.cibertec.sga.payment.application.IPaymentService;
import com.cibertec.sga.payment.domain.error.PaymentError;
import com.cibertec.sga.payment.domain.model.Payment;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * Cubre RNF-04 ("mantener consistencia entre cuentas pagadas, recibos y caja/banco") y RNF-05
 * ("garantizar correlativos únicos ante operaciones simultáneas") con concurrencia real de hilos
 * contra el pool de conexiones de Postgres.
 *
 * <p>Deliberadamente SIN {@code @Transactional} a nivel de clase, a diferencia del resto de
 * {@code *IntegrationTest} del proyecto: el rollback administrado por Spring envuelve toda la
 * ejecución del método de test (incluidas las llamadas concurrentes) en una única transacción
 * sobre una única conexión JDBC, lo que anula por completo la concurrencia real que este test
 * necesita observar — cada hilo debe abrir su propia transacción/conexión, tal como ocurriría en
 * producción con solicitudes HTTP concurrentes reales. Como consecuencia, los datos que crea
 * quedan persistidos en el contenedor Postgres compartido entre clases de test (patrón
 * "singleton container" de {@link AbstractIntegrationTest}); se usan códigos de socio con
 * sufijo aleatorio para no colisionar con fixtures de otras clases.
 */
@AutoConfigureMockMvc
class PaymentConcurrencyIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IPaymentService paymentService;

    private String authHeader;
    private String stage1Uuid;
    private String memberServiceUuid;

    @BeforeEach
    void loginAndCreateReferences() throws Exception {
        MvcResult login = mockMvc.perform(
            post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content("""
                {"username": "admin", "password": "Admin123!"}
                """)
        ).andReturn();
        authHeader = "Bearer " + objectMapper.readTree(login.getResponse().getContentAsString()).get("accessToken").asString();

        MvcResult stages = mockMvc.perform(get("/api/stages").header("Authorization", authHeader)).andReturn();
        stage1Uuid = findStageUuidByCode(objectMapper.readTree(stages.getResponse().getContentAsString()), 1);

        MvcResult recurrenceTypes = mockMvc.perform(get("/api/recurrence-types").header("Authorization", authHeader)).andReturn();
        String recurrenceTypeUuid = findUuidByName(objectMapper.readTree(recurrenceTypes.getResponse().getContentAsString()), "Monthly");

        MvcResult chargeTargetTypes = mockMvc.perform(get("/api/charge-target-types").header("Authorization", authHeader)).andReturn();
        String memberChargeTargetTypeUuid =
            findUuidByName(objectMapper.readTree(chargeTargetTypes.getResponse().getContentAsString()), "Member");

        MvcResult currencies = mockMvc.perform(get("/api/currencies").header("Authorization", authHeader)).andReturn();
        String currencyUuid = objectMapper.readTree(currencies.getResponse().getContentAsString()).get(0).get("uuid").asString();

        MvcResult service = mockMvc.perform(
            post("/api/services").header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"name": "Cuota Socio Concurrencia %s", "recurrenceTypeUuid": "%s", "chargeTargetTypeUuid": "%s",
                 "currencyUuid": "%s", "consumptionBased": false, "cost": 50.00}
                """.formatted(uniqueSuffix(), recurrenceTypeUuid, memberChargeTargetTypeUuid, currencyUuid))
        ).andExpect(status().isCreated()).andReturn();
        memberServiceUuid = objectMapper.readTree(service.getResponse().getContentAsString()).get("uuid").asString();
    }

    private String uniqueSuffix() {
        return UUID.randomUUID().toString().substring(0, 8);
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

    private String createMemberReceivable(String codeSuffix) throws Exception {
        String code = "CONC-" + codeSuffix;
        mockMvc.perform(
            post("/api/members").header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"code": "%s", "firstName": "Concurrencia", "lastName": "%s", "stageUuid": "%s"}
                """.formatted(code, codeSuffix, stage1Uuid))
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
            if (node.get("member").get("fullName").asString().equals("Concurrencia " + codeSuffix)) {
                return node.get("uuid").asString();
            }
        }
        throw new IllegalStateException("No se encontró la cuenta por cobrar generada para " + code);
    }

    /**
     * RNF-04: dos operaciones concurrentes de pago sobre la MISMA cuenta por cobrar no deben
     * producir dos pagos/recibos — el bloqueo pesimista agregado en la Fase 7 debe serializarlas,
     * dejando exactamente una exitosa y el resto rechazadas con 409 (cuenta ya no pendiente).
     */
    @Test
    void concurrentPaymentsOnSameReceivableOnlyOneSucceeds() throws Exception {
        String receivableUuid = createMemberReceivable(uniqueSuffix());
        int attempts = 8;
        List<Callable<Result<Payment, PaymentError>>> tasks = IntStream.range(0, attempts)
            .<Callable<Result<Payment, PaymentError>>>mapToObj(i -> () -> paymentService.processPayment(List.of(UUID.fromString(receivableUuid))))
            .toList();

        List<Result<Payment, PaymentError>> results = runConcurrently(tasks);

        long successes = results.stream().filter(Result::isSuccess).count();
        long notPendingConflicts = results.stream()
            .filter(r -> r.isFailure() && r.getError() instanceof PaymentError.AccountReceivableNotPending)
            .count();

        assertThat(successes).isEqualTo(1);
        assertThat(notPendingConflicts).isEqualTo(attempts - 1);

        mockMvc.perform(get("/api/account-receivables/{uuid}", receivableUuid).header("Authorization", authHeader))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status.name").value("Paid"));
    }

    /**
     * RNF-05: procesar pagos concurrentes sobre cuentas por cobrar DISTINTAS no debe colisionar
     * — cada uno debe emitir un recibo con correlativo único (la secuencia de Postgres detrás de
     * {@code fn_receipt_assign_correlative} es, en teoría, libre de bloqueos entre sí misma;
     * este test lo verifica empíricamente en vez de solo confiar en la garantía documental).
     */
    @Test
    void concurrentPaymentsAcrossDifferentReceivablesGetUniqueCorrelativos() throws Exception {
        int count = 10;
        List<String> receivableUuids = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            receivableUuids.add(createMemberReceivable(uniqueSuffix()));
        }

        List<Callable<Result<Payment, PaymentError>>> tasks = receivableUuids.stream()
            .<Callable<Result<Payment, PaymentError>>>map(uuid -> () -> paymentService.processPayment(List.of(UUID.fromString(uuid))))
            .toList();

        List<Result<Payment, PaymentError>> results = runConcurrently(tasks);

        assertThat(results).allMatch(Result::isSuccess);
        Set<Long> correlativos = results.stream()
            .map(r -> r.getValue().getReceipt().getCorrelativeNumber())
            .collect(Collectors.toSet());
        assertThat(correlativos).hasSize(count);
    }

    private <T> List<T> runConcurrently(List<Callable<T>> tasks) throws Exception {
        int parallelism = tasks.size();
        ExecutorService executor = Executors.newFixedThreadPool(parallelism);
        CountDownLatch ready = new CountDownLatch(parallelism);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<T>> futures = new ArrayList<>();
        try {
            for (Callable<T> task : tasks) {
                futures.add(executor.submit(() -> {
                    ready.countDown();
                    start.await();
                    return task.call();
                }));
            }
            ready.await(10, TimeUnit.SECONDS);
            start.countDown();

            List<T> results = new ArrayList<>();
            for (Future<T> future : futures) {
                results.add(future.get(15, TimeUnit.SECONDS));
            }
            return results;
        } finally {
            executor.shutdown();
        }
    }
}
