package toko_online.e2e;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class TransactionConcurrencyIT {

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
    @DisplayName("Concurrency: 20 concurrent purchase threads competing for 10 items in stock (no overselling)")
    void testConcurrentPurchases() throws InterruptedException {
        User buyer = TestDataFactory.regularUser();
        buyer.setPassword("$2a$12$6/1wL.P2k6Fm3o6lCqQ.m.5b1vV1m1E6yAEv7pM7M3g8tJkMkWmvy");
        userRepository.save(buyer);

        Product initialProduct = TestDataFactory.createProduct("Flash Sale Item", 10000.0, 10);
        Product savedProduct = productRepository.save(initialProduct);

        LoginRequest loginReq = TestDataFactory.validLoginRequest(buyer.getEmail(), "Admin123!");
        ResponseEntity<ApiResponse> loginResp = restTemplate.postForEntity("/api/v1/auth/login", loginReq, ApiResponse.class);
        Map<String, Object> loginData = (Map<String, Object>) loginResp.getBody().getData();
        String userToken = (String) loginData.get("accessToken");

        int totalThreads = 20;
        ExecutorService executor = Executors.newFixedThreadPool(totalThreads);
        CountDownLatch readyLatch = new CountDownLatch(totalThreads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(totalThreads);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger conflictCount = new AtomicInteger(0);

        for (int i = 0; i < totalThreads; i++) {
            executor.submit(() -> {
                readyLatch.countDown();
                try {
                    startLatch.await();
                    TransactionRequest req = TestDataFactory.validTransactionRequest(savedProduct.getId(), 1, buyer.getEmail(), 10000);
                    HttpHeaders headers = new HttpHeaders();
                    headers.setBearerAuth(userToken);
                    HttpEntity<TransactionRequest> entity = new HttpEntity<>(req, headers);

                    ResponseEntity<ApiResponse> response = restTemplate.postForEntity("/api/v1/transactions", entity, ApiResponse.class);
                    if (response.getStatusCode() == HttpStatus.CREATED) {
                        successCount.incrementAndGet();
                    } else if (response.getStatusCode() == HttpStatus.CONFLICT) {
                        conflictCount.incrementAndGet();
                    }
                } catch (Exception ignored) {
                } finally {
                    finishLatch.countDown();
                }
            });
        }

        readyLatch.await(10, TimeUnit.SECONDS);
        startLatch.countDown(); // trigger concurrent bursts
        finishLatch.await(30, TimeUnit.SECONDS);
        executor.shutdown();

        assertThat(successCount.get()).isEqualTo(10);
        assertThat(conflictCount.get()).isEqualTo(10);

        Product finalProduct = productRepository.findById(savedProduct.getId()).orElseThrow();
        assertThat(finalProduct.getStock()).isEqualTo(0);
    }
}
