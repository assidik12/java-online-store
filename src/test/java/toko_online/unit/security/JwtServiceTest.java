package toko_online.unit.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import toko_online.model.entity.User;
import toko_online.model.enums.Role;
import toko_online.security.JwtService;
import toko_online.support.JwtTestTokens;
import toko_online.support.TestDataFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = JwtTestTokens.createTestJwtService(60_000, 3600_000);
    }

    @Test
    @DisplayName("constructor: secret less than 32 bytes should throw IllegalStateException")
    void constructor_shortSecret_throwsIllegalStateException() {
        // Base64 encoding of "short" is "c2hvcnQ="
        assertThatThrownBy(() -> new JwtService("c2hvcnQ=", 60000, 3600000, "issuer"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("at least 256 bits");
    }

    @Test
    @DisplayName("generateAccessToken and parse claims: valid user details preserved")
    void generateAccessToken_and_parseClaims() {
        User user = TestDataFactory.createUser("charlie", "charlie@test.com", Role.USER);
        user.setId(505L);

        String token = jwtService.generateAccessToken(user);

        assertThat(token).isNotBlank();
        assertThat(jwtService.isTokenValid(token)).isTrue();
        assertThat(jwtService.extractUserId(token)).isEqualTo(505L);
        assertThat(jwtService.extractUsername(token)).isEqualTo("charlie");
        assertThat(jwtService.extractRole(token)).isEqualTo(Role.USER);
    }

    @Test
    @DisplayName("generateRefreshToken: includes type=refresh claim")
    void generateRefreshToken_valid() {
        User user = TestDataFactory.createUser("charlie", "charlie@test.com", Role.USER);
        user.setId(505L);

        String token = jwtService.generateRefreshToken(user);

        assertThat(token).isNotBlank();
        assertThat(jwtService.isTokenValid(token)).isTrue();
        assertThat(jwtService.extractUserId(token)).isEqualTo(505L);
        assertThat(jwtService.parseClaims(token).get("type")).isEqualTo("refresh");
    }

    @Test
    @DisplayName("parseClaims: expired token throws ExpiredJwtException")
    void parseClaims_expired_throwsExpiredJwtException() throws InterruptedException {
        JwtService shortLivedJwt = JwtTestTokens.createTestJwtService(10, 100);
        User user = TestDataFactory.regularUser();

        String token = shortLivedJwt.generateAccessToken(user);
        Thread.sleep(25);

        assertThat(shortLivedJwt.isTokenValid(token)).isFalse();
        assertThatThrownBy(() -> shortLivedJwt.parseClaims(token))
                .isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    @DisplayName("parseClaims: wrong signature throws SignatureException")
    void parseClaims_wrongSignature_throwsSignatureException() {
        String otherSecret = "YW5vdGhlci1zZWNyZXQta2V5LWZvci1qd3QtaG1hYy1zaGEyNTYtMTIzNDU2Nzg5MDEyMzQ1Ng==";
        JwtService otherJwt = new JwtService(otherSecret, 60000, 3600000, JwtTestTokens.TEST_ISSUER);

        User user = TestDataFactory.regularUser();
        String tokenFromOther = otherJwt.generateAccessToken(user);

        assertThat(jwtService.isTokenValid(tokenFromOther)).isFalse();
        assertThatThrownBy(() -> jwtService.parseClaims(tokenFromOther))
                .isInstanceOf(SignatureException.class);
    }

    @Test
    @DisplayName("parseClaims: malformed token throws MalformedJwtException")
    void parseClaims_malformed_throwsMalformedJwtException() {
        String token = JwtTestTokens.malformedToken();

        assertThat(jwtService.isTokenValid(token)).isFalse();
        assertThatThrownBy(() -> jwtService.parseClaims(token))
                .isInstanceOf(MalformedJwtException.class);
    }
}
