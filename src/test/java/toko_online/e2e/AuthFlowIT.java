package toko_online.e2e;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import toko_online.model.dto.request.LoginRequest;
import toko_online.model.dto.request.RegisterRequest;
import toko_online.model.dto.response.ApiResponse;
import toko_online.support.TestDataFactory;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AuthFlowIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setup() {
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
        jdbcTemplate.execute("TRUNCATE TABLE users");
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
    }

    @Test
    @DisplayName("End-to-End: Register -> Login -> /users/me")
    void testAuthLifecycle() {
        // 1. Register
        RegisterRequest regReq = TestDataFactory.validRegisterRequest("e2e_user", "e2e@store.com");
        ResponseEntity<ApiResponse> regResp = restTemplate.postForEntity("/api/v1/auth/register", regReq, ApiResponse.class);
        assertThat(regResp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(regResp.getBody().isSuccess()).isTrue();

        // 2. Login
        LoginRequest loginReq = TestDataFactory.validLoginRequest("e2e@store.com", "Secret123!");
        ResponseEntity<ApiResponse> loginResp = restTemplate.postForEntity("/api/v1/auth/login", loginReq, ApiResponse.class);
        assertThat(loginResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(loginResp.getBody().isSuccess()).isTrue();

        Map<String, Object> data = (Map<String, Object>) loginResp.getBody().getData();
        String accessToken = (String) data.get("accessToken");
        assertThat(accessToken).isNotBlank();

        // 3. Access Protected Endpoint /api/v1/users/me
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<ApiResponse> meResp = restTemplate.exchange("/api/v1/users/me", HttpMethod.GET, entity, ApiResponse.class);
        assertThat(meResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(meResp.getBody().isSuccess()).isTrue();
    }
}
