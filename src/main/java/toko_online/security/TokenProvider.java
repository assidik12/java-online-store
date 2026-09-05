package toko_online.security;

import io.jsonwebtoken.Claims;
import toko_online.model.entity.User;
import toko_online.model.enums.Role;

import java.util.function.Function;

public interface TokenProvider {
    String generateAccessToken(User user);
    String generateRefreshToken(User user);
    long getAccessTokenExpirationSeconds();
    Claims parseClaims(String token);
    <T> T extractClaim(String token, Function<Claims, T> resolver);
    Long extractUserId(String token);
    String extractUsername(String token);
    Role extractRole(String token);
    boolean isTokenValid(String token);
}
