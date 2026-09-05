package toko_online.slice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import toko_online.controller.AuthController;
import toko_online.exception.UnauthorizedException;
import toko_online.model.dto.request.LoginRequest;
import toko_online.model.dto.request.RegisterRequest;
import toko_online.model.dto.response.LoginResponse;
import toko_online.model.dto.response.UserResponse;
import toko_online.model.enums.Role;
import toko_online.security.AppUserDetailsService;
import toko_online.security.JwtAuthenticationFilter;
import toko_online.security.TokenProvider;
import toko_online.service.AuthService;
import toko_online.support.TestDataFactory;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private TokenProvider tokenProvider;

    @MockBean
    private AppUserDetailsService appUserDetailsService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @DisplayName("POST /api/v1/auth/register: valid body returns 201 Created")
    void register_valid_returns201() throws Exception {
        RegisterRequest req = TestDataFactory.validRegisterRequest("alice", "alice@example.com");
        UserResponse res = new UserResponse(1L, "alice", "alice@example.com", Role.USER, "08123", "Jl.", "123");
        when(authService.register(any(RegisterRequest.class))).thenReturn(res);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value("alice"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/register: invalid body returns 400 Bad Request")
    void register_invalid_returns400() throws Exception {
        RegisterRequest req = new RegisterRequest(); // empty fields violate validation

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("POST /api/v1/auth/login: valid credentials returns 200 with tokens")
    void login_valid_returns200() throws Exception {
        LoginRequest req = TestDataFactory.validLoginRequest("alice@example.com", "secret123");
        LoginResponse res = new LoginResponse(1L, "alice@example.com", "alice", "USER", "access.tok", "refresh.tok", "Bearer", 300L);
        when(authService.login(any(LoginRequest.class))).thenReturn(res);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("access.tok"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/login: wrong credentials returns 401")
    void login_wrongCredentials_returns401() throws Exception {
        LoginRequest req = TestDataFactory.validLoginRequest("alice@example.com", "wrong");
        when(authService.login(any(LoginRequest.class))).thenThrow(new UnauthorizedException("Email atau password salah."));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("POST /api/v1/auth/refresh: valid token returns 200 with new tokens")
    void refresh_valid_returns200() throws Exception {
        LoginResponse res = new LoginResponse(1L, "alice@example.com", "alice", "USER", "new.access", "new.refresh", "Bearer", 300L);
        when(authService.refresh(eq("sample.refresh.token"))).thenReturn(res);

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .header("Authorization", "Bearer sample.refresh.token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").value("new.access"));
    }
}
