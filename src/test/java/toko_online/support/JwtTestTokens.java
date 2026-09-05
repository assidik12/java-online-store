package toko_online.support;

import toko_online.model.entity.User;
import toko_online.model.enums.Role;
import toko_online.security.JwtService;

public class JwtTestTokens {

    public static final String TEST_SECRET = "dGVzdC1zZWNyZXQta2V5LWZvci1qd3QtaG1hYy1zaGEyNTYtZG8tbm90LXVzZS1pbi1wcm9kdWN0aW9uLXBsZWFzZS1jaGFuZ2UtbWU=";
    public static final String TEST_ISSUER = "retailflow-test";

    public static JwtService createTestJwtService(long accessTtlMs, long refreshTtlMs) {
        return new JwtService(TEST_SECRET, accessTtlMs, refreshTtlMs, TEST_ISSUER);
    }

    public static JwtService createTestJwtService() {
        return createTestJwtService(300_000, 3600_000);
    }

    public static String validAccessToken(User user) {
        return createTestJwtService().generateAccessToken(user);
    }

    public static String validRefreshToken(User user) {
        return createTestJwtService().generateRefreshToken(user);
    }

    public static String malformedToken() {
        return "this.is.not.a.valid.jwt.token";
    }
}
