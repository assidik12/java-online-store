package toko_online.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import toko_online.model.dto.request.LoginRequest;
import toko_online.model.dto.request.RegisterRequest;
import toko_online.model.dto.response.ApiResponse;
import toko_online.model.dto.response.LoginResponse;
import toko_online.model.dto.response.UserResponse;
import toko_online.service.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Registrasi, login, dan refresh JWT token.")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private static final String BEARER_PREFIX = "Bearer ";

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = "Registrasi user baru (default role: USER)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Registrasi berhasil",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validasi gagal / duplikat")
    })
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody RegisterRequest request) {
        log.info("REST Request: POST /api/v1/auth/register untuk username: {}", request.getUsername());
        UserResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Registrasi user berhasil.", response));
    }

    @PostMapping("/login")
    @Operation(summary = "Login dengan email/username & password, mengembalikan JWT access + refresh token")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login berhasil",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Kredensial salah")
    })
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("REST Request: POST /api/v1/auth/login untuk identifier: {}", request.getUserEmail());
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.ok("Login berhasil.", response));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Perbarui access token menggunakan refresh token",
            description = "Refresh token dapat dikirim via header `Authorization: Bearer <token>` atau body `{\"refreshToken\": \"...\"}`.")
    public ResponseEntity<ApiResponse<LoginResponse>> refresh(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestBody(required = false) java.util.Map<String, String> body) {
        String token = null;
        if (authorizationHeader != null && authorizationHeader.startsWith(BEARER_PREFIX)) {
            token = authorizationHeader.substring(BEARER_PREFIX.length());
        } else if (body != null) {
            token = body.get("refreshToken");
        }
        log.info("REST Request: POST /api/v1/auth/refresh");
        LoginResponse response = authService.refresh(token);
        return ResponseEntity.ok(ApiResponse.ok("Token berhasil diperbarui.", response));
    }
}
