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
import toko_online.model.dto.request.ProductRequest;
import toko_online.model.dto.response.ApiResponse;
import toko_online.model.entity.User;
import toko_online.model.enums.Role;
import toko_online.repository.UserRepository;
import toko_online.support.TestDataFactory;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ProductFlowIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setup() {
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
        jdbcTemplate.execute("TRUNCATE TABLE products");
        jdbcTemplate.execute("TRUNCATE TABLE users");
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
    }

    @Test
    @DisplayName("End-to-End: Admin creates product, public reads product")
    void testProductLifecycle() {
        // 1. Seed Admin user directly
        User admin = TestDataFactory.adminUser();
        // BCrypt hash for "Admin123!"
        admin.setPassword("$2a$12$6/1wL.P2k6Fm3o6lCqQ.m.5b1vV1m1E6yAEv7pM7M3g8tJkMkWmvy");
        userRepository.save(admin);

        // 2. Login as admin
        LoginRequest loginReq = TestDataFactory.validLoginRequest(admin.getEmail(), "Admin123!");
        ResponseEntity<ApiResponse> loginResp = restTemplate.postForEntity("/api/v1/auth/login", loginReq, ApiResponse.class);
        Map<String, Object> loginData = (Map<String, Object>) loginResp.getBody().getData();
        String adminToken = (String) loginData.get("accessToken");

        // 3. Admin creates product
        ProductRequest prodReq = TestDataFactory.validProductRequest("Gaming Mouse", 350000.0, 15);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);
        HttpEntity<ProductRequest> createEntity = new HttpEntity<>(prodReq, headers);

        ResponseEntity<ApiResponse> createResp = restTemplate.postForEntity("/api/v1/products", createEntity, ApiResponse.class);
        assertThat(createResp.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // 4. Public reads products
        ResponseEntity<ApiResponse> listResp = restTemplate.getForEntity("/api/v1/products", ApiResponse.class);
        assertThat(listResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(listResp.getBody().isSuccess()).isTrue();
    }
}
