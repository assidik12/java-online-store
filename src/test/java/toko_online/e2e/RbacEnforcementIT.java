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
class RbacEnforcementIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("Unauthenticated request to protected endpoint /api/v1/users/me returns 401")
    void unauthenticatedAccess_returns401() {
        ResponseEntity<ApiResponse> response = restTemplate.getForEntity("/api/v1/users/me", ApiResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("Public endpoint /api/v1/products accessible without token returns 200")
    void publicAccess_returns200() {
        ResponseEntity<ApiResponse> response = restTemplate.getForEntity("/api/v1/products", ApiResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
