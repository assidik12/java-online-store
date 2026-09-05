package toko_online.e2e;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import toko_online.model.dto.response.ApiResponse;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class GlobalExceptionHandlerIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("Requesting non-existent product returns 404 with structured ApiResponse")
    void handleNotFound_returns404() {
        ResponseEntity<ApiResponse> response = restTemplate.getForEntity("/api/v1/products/999999", ApiResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getMessage()).contains("tidak ditemukan");
    }
}
