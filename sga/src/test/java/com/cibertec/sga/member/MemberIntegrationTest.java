package com.cibertec.sga.member;

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
class MemberIntegrationTest extends AbstractIntegrationTest {

    private static final String BASE_URL = "/api/members";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String authHeader;
    private String stageUuid;

    @BeforeEach
    void loginAsAdminAndFetchStage() throws Exception {
        MvcResult login = mockMvc.perform(
            post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content("""
                {"username": "admin", "password": "Admin123!"}
                """)
        ).andReturn();
        String accessToken = objectMapper.readTree(login.getResponse().getContentAsString()).get("accessToken").asString();
        authHeader = "Bearer " + accessToken;

        MvcResult stages = mockMvc.perform(get("/api/stages").header("Authorization", authHeader)).andReturn();
        stageUuid = objectMapper.readTree(stages.getResponse().getContentAsString()).get(0).get("uuid").asString();
    }

    private String createMember(String code) throws Exception {
        MvcResult created = mockMvc.perform(
            post(BASE_URL).header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"code": "%s", "firstName": "Juan", "lastName": "Perez", "stageUuid": "%s"}
                """.formatted(code, stageUuid))
        ).andExpect(status().isCreated()).andReturn();
        return objectMapper.readTree(created.getResponse().getContentAsString()).get("uuid").asString();
    }

    @Test
    void createThenAppearsInListAndGetByUuid() throws Exception {
        String uuid = createMember("M-001");

        mockMvc.perform(get(BASE_URL + "/{uuid}", uuid).header("Authorization", authHeader))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("Juan"))
            .andExpect(jsonPath("$.stage.uuid").value(stageUuid))
            .andExpect(jsonPath("$.active").value(true));

        mockMvc.perform(get(BASE_URL).param("search", "M-001").header("Authorization", authHeader))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].code").value("M-001"));
    }

    @Test
    void createWithDuplicateCodeReturnsConflict() throws Exception {
        createMember("M-002");

        mockMvc.perform(
            post(BASE_URL).header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"code": "M-002", "firstName": "Otro", "lastName": "Socio", "stageUuid": "%s"}
                """.formatted(stageUuid))
        )
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.error").value("MEMBER_DUPLICATE_CODE"));
    }

    @Test
    void createWithBlankFirstNameReturnsBadRequest() throws Exception {
        mockMvc.perform(
            post(BASE_URL).header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"code": "M-003", "firstName": "", "lastName": "Perez", "stageUuid": "%s"}
                """.formatted(stageUuid))
        ).andExpect(status().isBadRequest());
    }

    @Test
    void createWithUnknownStageReturnsBadRequest() throws Exception {
        mockMvc.perform(
            post(BASE_URL).header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"code": "M-004", "firstName": "Juan", "lastName": "Perez", "stageUuid": "%s"}
                """.formatted(UUID.randomUUID()))
        )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("MEMBER_STAGE_NOT_FOUND"));
    }

    @Test
    void updateExistingMemberChangesName() throws Exception {
        String uuid = createMember("M-005");

        mockMvc.perform(
            put(BASE_URL + "/{uuid}", uuid)
                .header("Authorization", authHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"code": "M-005", "firstName": "Juan Carlos", "lastName": "Perez", "stageUuid": "%s"}
                    """.formatted(stageUuid))
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("Juan Carlos"));
    }

    @Test
    void updateNonExistentUuidReturnsNotFound() throws Exception {
        mockMvc.perform(
            put(BASE_URL + "/{uuid}", UUID.randomUUID())
                .header("Authorization", authHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"code": "M-999", "firstName": "No", "lastName": "Existe", "stageUuid": "%s"}
                    """.formatted(stageUuid))
        ).andExpect(status().isNotFound());
    }

    @Test
    void deactivateExistingMemberFlipsActiveAndIsFilterable() throws Exception {
        String uuid = createMember("M-006");

        mockMvc.perform(patch(BASE_URL + "/{uuid}/deactivate", uuid).header("Authorization", authHeader))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.active").value(false));

        mockMvc.perform(get(BASE_URL).param("search", "M-006").param("active", "true").header("Authorization", authHeader))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isEmpty());

        mockMvc.perform(get(BASE_URL).param("search", "M-006").param("active", "false").header("Authorization", authHeader))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].code").value("M-006"));
    }
}
