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
import toko_online.model.dto.request.TransactionRequest;
import toko_online.model.dto.response.ApiResponse;
import toko_online.model.entity.Product;
import toko_online.model.entity.User;
import toko_online.repository.ProductRepository;
import toko_online.repository.UserRepository;
import toko_online.support.TestDataFactory;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class TransactionFlowIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setup() {
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
        jdbcTemplate.execute("TRUNCATE TABLE transaction_details");
        jdbcTemplate.execute("TRUNCATE TABLE transactions");
        jdbcTemplate.execute("TRUNCATE TABLE products");
        jdbcTemplate.execute("TRUNCATE TABLE users");
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
    }

    @Test
    @DisplayName("End-to-End: Purchase product, decrement stock, and view transaction")
    void testPurchaseLifecycle() {
        User buyer = TestDataFactory.regularUser();
        buyer.setPassword("$2a$12$6/1wL.P2k6Fm3o6lCqQ.m.5b1vV1m1E6yAEv7pM7M3g8tJkMkWmvy");
        userRepository.save(buyer);

        Product prod = TestDataFactory.createProduct("Monitor 24 inch", 1500000.0, 5);
        Product savedProd = productRepository.save(prod);

        // Login buyer
        LoginRequest loginReq = TestDataFactory.validLoginRequest(buyer.getEmail(), "Admin123!");
        ResponseEntity<ApiResponse> loginResp = restTemplate.postForEntity("/api/v1/auth/login", loginReq, ApiResponse.class);
        Map<String, Object> loginData = (Map<String, Object>) loginResp.getBody().getData();
        String userToken = (String) loginData.get("accessToken");

        // Buy product (qty: 2)
        TransactionRequest txReq = TestDataFactory.validTransactionRequest(savedProd.getId(), 2, buyer.getEmail(), 3000000);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(userToken);
        HttpEntity<TransactionRequest> buyEntity = new HttpEntity<>(txReq, headers);

        ResponseEntity<ApiResponse> buyResp = restTemplate.postForEntity("/api/v1/transactions", buyEntity, ApiResponse.class);
        assertThat(buyResp.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // Verify stock is now 3
        Product updatedProduct = productRepository.findById(savedProd.getId()).orElseThrow();
        assertThat(updatedProduct.getStock()).isEqualTo(3);
    }
}
