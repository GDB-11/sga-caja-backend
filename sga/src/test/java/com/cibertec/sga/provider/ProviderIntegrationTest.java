package com.cibertec.sga.provider;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
import tools.jackson.databind.ObjectMapper;

@AutoConfigureMockMvc
@Transactional
class ProviderIntegrationTest extends AbstractIntegrationTest {

    private static final String BASE_URL = "/api/providers";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String authHeader;

    @BeforeEach
    void loginAsAdmin() throws Exception {
        MvcResult login = mockMvc.perform(
            post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content("""
                {"username": "admin", "password": "Admin123!"}
                """)
        ).andReturn();
        String accessToken = objectMapper.readTree(login.getResponse().getContentAsString()).get("accessToken").asText();
        authHeader = "Bearer " + accessToken;
    }

    private String createProvider(String name) throws Exception {
        MvcResult created = mockMvc.perform(
            post(BASE_URL).header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"name": "%s", "document": "20123456789"}
                """.formatted(name))
        ).andExpect(status().isCreated()).andReturn();
        return objectMapper.readTree(created.getResponse().getContentAsString()).get("uuid").asText();
    }

    @Test
    void createThenAppearsInListAndGetByUuid() throws Exception {
        String uuid = createProvider("Distribuidora Lima");

        mockMvc.perform(get(BASE_URL + "/{uuid}", uuid).header("Authorization", authHeader))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Distribuidora Lima"))
            .andExpect(jsonPath("$.active").value(true));

        mockMvc.perform(get(BASE_URL).param("search", "Distribuidora").header("Authorization", authHeader))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].name").value("Distribuidora Lima"));
    }

    @Test
    void createWithBlankNameReturnsBadRequest() throws Exception {
        mockMvc.perform(
            post(BASE_URL).header("Authorization", authHeader).contentType(MediaType.APPLICATION_JSON).content("""
                {"name": ""}
                """)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void updateExistingProviderChangesName() throws Exception {
        String uuid = createProvider("Proveedor Original");

        mockMvc.perform(
            put(BASE_URL + "/{uuid}", uuid)
                .header("Authorization", authHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"name": "Proveedor Renombrado", "document": "20123456789"}
                    """)
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Proveedor Renombrado"));
    }

    @Test
    void deactivateExistingProviderFlipsActive() throws Exception {
        String uuid = createProvider("Proveedor a desactivar");

        mockMvc.perform(patch(BASE_URL + "/{uuid}/deactivate", uuid).header("Authorization", authHeader))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.active").value(false));
    }
}
