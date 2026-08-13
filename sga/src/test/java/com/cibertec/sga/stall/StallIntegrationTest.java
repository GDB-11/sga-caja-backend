package com.cibertec.sga.stall;

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
class StallIntegrationTest extends AbstractIntegrationTest {

    private static final String BASE_URL = "/api/stalls";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String authHeader;
    private String businessTypeUuid;
    private String memberUuid;

    @BeforeEach
    void loginAndCreateReferences() throws Exception {
        MvcResult login = mockMvc.perform(
            post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content("""
                {"username": "admin", "password": "Admin123!"}
                """)
        ).andReturn();
        String accessToken = objectMapper.readTree(login.getResponse().getContentAsString()).get("accessToken").asString();
        authHeader = "Bearer " + accessToken;

        MvcResult businessType = mockMvc.perform(
            post("/api/business-types").header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"name": "Abarrotes Stall Test"}
                """)
        ).andExpect(status().isCreated()).andReturn();
        businessTypeUuid = objectMapper.readTree(businessType.getResponse().getContentAsString()).get("uuid").asString();

        MvcResult stages = mockMvc.perform(get("/api/stages").header("Authorization", authHeader)).andReturn();
        String stageUuid = objectMapper.readTree(stages.getResponse().getContentAsString()).get(0).get("uuid").asString();

        MvcResult member = mockMvc.perform(
            post("/api/members").header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"code": "STALL-M-001", "firstName": "Ana", "lastName": "Ruiz", "stageUuid": "%s"}
                """.formatted(stageUuid))
        ).andExpect(status().isCreated()).andReturn();
        memberUuid = objectMapper.readTree(member.getResponse().getContentAsString()).get("uuid").asString();
    }

    private String createStall(String number, String memberUuidOrNull) throws Exception {
        String memberField = memberUuidOrNull == null ? "null" : "\"" + memberUuidOrNull + "\"";
        MvcResult created = mockMvc.perform(
            post(BASE_URL).header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"number": "%s", "businessTypeUuid": "%s", "memberUuid": %s}
                """.formatted(number, businessTypeUuid, memberField))
        ).andExpect(status().isCreated()).andReturn();
        return objectMapper.readTree(created.getResponse().getContentAsString()).get("uuid").asString();
    }

    @Test
    void createWithMemberThenAppearsInListAndGetByUuid() throws Exception {
        String uuid = createStall("P-001", memberUuid);

        mockMvc.perform(get(BASE_URL + "/{uuid}", uuid).header("Authorization", authHeader))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.businessType.uuid").value(businessTypeUuid))
            .andExpect(jsonPath("$.member.uuid").value(memberUuid))
            .andExpect(jsonPath("$.member.fullName").value("Ana Ruiz"));

        mockMvc.perform(get(BASE_URL).param("search", "P-001").header("Authorization", authHeader))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].number").value("P-001"));
    }

    @Test
    void createWithoutMemberLeavesMemberNull() throws Exception {
        String uuid = createStall("P-002", null);

        mockMvc.perform(get(BASE_URL + "/{uuid}", uuid).header("Authorization", authHeader))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.member").doesNotExist());
    }

    @Test
    void createWithDuplicateNumberReturnsConflict() throws Exception {
        createStall("P-003", null);

        mockMvc.perform(
            post(BASE_URL).header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"number": "P-003", "businessTypeUuid": "%s"}
                """.formatted(businessTypeUuid))
        )
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.error").value("STALL_DUPLICATE_NUMBER"));
    }

    @Test
    void createWithUnknownBusinessTypeReturnsBadRequest() throws Exception {
        mockMvc.perform(
            post(BASE_URL).header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"number": "P-004", "businessTypeUuid": "%s"}
                """.formatted(UUID.randomUUID()))
        )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("STALL_BUSINESS_TYPE_NOT_FOUND"));
    }

    @Test
    void createWithInvalidValidityPeriodReturnsBadRequest() throws Exception {
        mockMvc.perform(
            post(BASE_URL).header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"number": "P-005", "businessTypeUuid": "%s", "validityStartDate": "2026-06-01", "validityEndDate": "2026-01-01"}
                """.formatted(businessTypeUuid))
        )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("STALL_INVALID_VALIDITY_PERIOD"));
    }

    @Test
    void deactivateExistingStallFlipsActive() throws Exception {
        String uuid = createStall("P-006", null);

        mockMvc.perform(patch(BASE_URL + "/{uuid}/deactivate", uuid).header("Authorization", authHeader))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.active").value(false));
    }
}
