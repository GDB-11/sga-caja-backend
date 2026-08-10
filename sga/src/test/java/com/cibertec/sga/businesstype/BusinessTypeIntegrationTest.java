package com.cibertec.sga.businesstype;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cibertec.sga.common.AbstractIntegrationTest;
import tools.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@AutoConfigureMockMvc
@Transactional
class BusinessTypeIntegrationTest extends AbstractIntegrationTest {

    private static final String BASE_URL = "/api/business-types";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createThenAppearsInListAndGetByUuid() throws Exception {
        MvcResult created = mockMvc.perform(
            post(BASE_URL).contentType(MediaType.APPLICATION_JSON).content("""
                {"name": "Abarrotes"}
                """)
        ).andExpect(status().isCreated()).andReturn();

        String uuid = objectMapper.readTree(created.getResponse().getContentAsString()).get("uuid").asText();

        mockMvc.perform(get(BASE_URL + "/{uuid}", uuid))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Abarrotes"));

        mockMvc.perform(get(BASE_URL))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", org.hamcrest.Matchers.hasItem(
                org.hamcrest.Matchers.hasEntry("name", "Abarrotes")
            )));
    }

    @Test
    void createWithDuplicateNameReturnsConflict() throws Exception {
        mockMvc.perform(
            post(BASE_URL).contentType(MediaType.APPLICATION_JSON).content("""
                {"name": "Ferreteria"}
                """)
        ).andExpect(status().isCreated());

        mockMvc.perform(
            post(BASE_URL).contentType(MediaType.APPLICATION_JSON).content("""
                {"name": "Ferreteria"}
                """)
        )
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.error").value("BUSINESS_TYPE_DUPLICATE_NAME"));
    }

    @Test
    void createWithBlankNameReturnsBadRequest() throws Exception {
        mockMvc.perform(
            post(BASE_URL).contentType(MediaType.APPLICATION_JSON).content("""
                {"name": ""}
                """)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void updateExistingBusinessTypeChangesName() throws Exception {
        MvcResult created = mockMvc.perform(
            post(BASE_URL).contentType(MediaType.APPLICATION_JSON).content("""
                {"name": "Ropa"}
                """)
        ).andExpect(status().isCreated()).andReturn();
        String uuid = objectMapper.readTree(created.getResponse().getContentAsString()).get("uuid").asText();

        mockMvc.perform(
            put(BASE_URL + "/{uuid}", uuid).contentType(MediaType.APPLICATION_JSON).content("""
                {"name": "Ropa y calzado"}
                """)
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Ropa y calzado"));
    }

    @Test
    void updateNonExistentUuidReturnsNotFound() throws Exception {
        mockMvc.perform(
            put(BASE_URL + "/{uuid}", UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"name": "No existe"}
                    """)
        ).andExpect(status().isNotFound());
    }

    @Test
    void deleteExistingBusinessTypeThenGetReturnsNotFound() throws Exception {
        MvcResult created = mockMvc.perform(
            post(BASE_URL).contentType(MediaType.APPLICATION_JSON).content("""
                {"name": "Electrodomesticos"}
                """)
        ).andExpect(status().isCreated()).andReturn();
        String uuid = objectMapper.readTree(created.getResponse().getContentAsString()).get("uuid").asText();

        mockMvc.perform(delete(BASE_URL + "/{uuid}", uuid)).andExpect(status().isNoContent());
        mockMvc.perform(get(BASE_URL + "/{uuid}", uuid)).andExpect(status().isNotFound());
    }
}
