package com.cibertec.sga.expensereason;

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
import tools.jackson.databind.ObjectMapper;

@AutoConfigureMockMvc
@Transactional
class ExpenseReasonIntegrationTest extends AbstractIntegrationTest {

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
        authHeader = "Bearer " + objectMapper.readTree(login.getResponse().getContentAsString()).get("accessToken").asString();
    }

    @Test
    void listReturnsSeededExpenseReasons() throws Exception {
        mockMvc.perform(get("/api/expense-reasons").header("Authorization", authHeader))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(12));
    }

    @Test
    void getByUuidReturnsMatchingExpenseReason() throws Exception {
        MvcResult list = mockMvc.perform(get("/api/expense-reasons").header("Authorization", authHeader)).andReturn();
        String uuid = objectMapper.readTree(list.getResponse().getContentAsString()).get(0).get("uuid").asString();

        mockMvc.perform(get("/api/expense-reasons/{uuid}", uuid).header("Authorization", authHeader))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.uuid").value(uuid));
    }

    @Test
    void getByUnknownUuidReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/expense-reasons/{uuid}", UUID.randomUUID()).header("Authorization", authHeader))
            .andExpect(status().isNotFound());
    }
}
