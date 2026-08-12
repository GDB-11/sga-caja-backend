package com.cibertec.sga.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cibertec.sga.common.AbstractIntegrationTest;
import jakarta.servlet.http.Cookie;
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
class AuthIntegrationTest extends AbstractIntegrationTest {

    private static final String LOGIN_URL = "/api/auth/login";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void loginWithValidCredentialsReturnsAccessTokenAndRefreshCookie() throws Exception {
        MvcResult result = login("admin", "Admin123!");

        assertEquals(200, result.getResponse().getStatus());
        var body = objectMapper.readTree(result.getResponse().getContentAsString());
        assertNotNull(body.get("accessToken").asText());
        assertEquals("Administrator", body.get("user").get("roleName").asText());

        Cookie refreshCookie = result.getResponse().getCookie("refreshToken");
        assertNotNull(refreshCookie);
        assertNotNull(refreshCookie.getValue());
    }

    @Test
    void loginWithWrongPasswordReturnsUnauthorized() throws Exception {
        mockMvc.perform(
            post(LOGIN_URL).contentType(MediaType.APPLICATION_JSON).content("""
                {"username": "admin", "password": "wrong-password"}
                """)
        )
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error").value("AUTH_INVALID_CREDENTIALS"));
    }

    @Test
    void loginWithUnknownUsernameReturnsUnauthorized() throws Exception {
        mockMvc.perform(
            post(LOGIN_URL).contentType(MediaType.APPLICATION_JSON).content("""
                {"username": "no-such-user", "password": "whatever"}
                """)
        )
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error").value("AUTH_INVALID_CREDENTIALS"));
    }

    @Test
    void meWithoutTokenReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/auth/me")).andExpect(status().isUnauthorized());
    }

    @Test
    void meWithValidAccessTokenReturnsProfile() throws Exception {
        MvcResult loginResult = login("cashier", "Cashier123!");
        String accessToken = accessTokenOf(loginResult);

        mockMvc.perform(get("/api/auth/me").header("Authorization", "Bearer " + accessToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("cashier"))
            .andExpect(jsonPath("$.roleName").value("CashierOperator"));
    }

    @Test
    void refreshRotatesTokenAndOldCookieNoLongerWorks() throws Exception {
        MvcResult loginResult = login("admin", "Admin123!");
        Cookie originalCookie = loginResult.getResponse().getCookie("refreshToken");

        MvcResult refreshResult = mockMvc.perform(post("/api/auth/refresh").cookie(originalCookie))
            .andExpect(status().isOk())
            .andReturn();
        Cookie rotatedCookie = refreshResult.getResponse().getCookie("refreshToken");

        assertNotNull(rotatedCookie);
        assertNotEquals(originalCookie.getValue(), rotatedCookie.getValue());
    }

    @Test
    void reusingRevokedRefreshTokenIsRejectedAndRevokesRotatedTokenToo() throws Exception {
        MvcResult loginResult = login("admin", "Admin123!");
        Cookie originalCookie = loginResult.getResponse().getCookie("refreshToken");

        MvcResult refreshResult = mockMvc.perform(post("/api/auth/refresh").cookie(originalCookie))
            .andExpect(status().isOk())
            .andReturn();
        Cookie rotatedCookie = refreshResult.getResponse().getCookie("refreshToken");

        // Reusing the already-rotated (now revoked) original cookie must be rejected...
        mockMvc.perform(post("/api/auth/refresh").cookie(originalCookie))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error").value("AUTH_REFRESH_TOKEN_REUSED"));

        // ...and must also kill the legitimately-rotated token (all sessions revoked on reuse).
        // It's now revoked too, so touching it is indistinguishable from reuse and reported the same way.
        mockMvc.perform(post("/api/auth/refresh").cookie(rotatedCookie))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error").value("AUTH_REFRESH_TOKEN_REUSED"));
    }

    @Test
    void logoutRevokesRefreshTokenSoItCanNoLongerBeUsed() throws Exception {
        MvcResult loginResult = login("admin", "Admin123!");
        Cookie refreshCookie = loginResult.getResponse().getCookie("refreshToken");

        mockMvc.perform(post("/api/auth/logout").cookie(refreshCookie)).andExpect(status().isNoContent());

        mockMvc.perform(post("/api/auth/refresh").cookie(refreshCookie))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void businessTypeWriteRequiresAdministratorRole() throws Exception {
        String cashierToken = accessTokenOf(login("cashier", "Cashier123!"));
        String adminToken = accessTokenOf(login("admin", "Admin123!"));
        String body = """
            {"name": "Repuestos"}
            """;

        mockMvc.perform(
            post("/api/business-types")
                .header("Authorization", "Bearer " + cashierToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        ).andExpect(status().isForbidden());

        mockMvc.perform(
            post("/api/business-types")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
        ).andExpect(status().isCreated());
    }

    @Test
    void businessTypeReadIsAllowedForAnyAuthenticatedRole() throws Exception {
        String cashierToken = accessTokenOf(login("cashier", "Cashier123!"));

        mockMvc.perform(get("/api/business-types").header("Authorization", "Bearer " + cashierToken))
            .andExpect(status().isOk());
    }

    private MvcResult login(String username, String password) throws Exception {
        return mockMvc.perform(
            post(LOGIN_URL).contentType(MediaType.APPLICATION_JSON).content(
                """
                {"username": "%s", "password": "%s"}
                """.formatted(username, password)
            )
        ).andReturn();
    }

    private String accessTokenOf(MvcResult loginResult) throws Exception {
        return objectMapper.readTree(loginResult.getResponse().getContentAsString()).get("accessToken").asText();
    }
}
