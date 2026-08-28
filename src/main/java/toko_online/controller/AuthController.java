package toko_online.controller;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    // Constructor Injection: Spring otomatis meng-inject bean AuthService
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody RegisterRequest request) {
        log.info("REST Request: POST /api/v1/auth/register untuk username: {}", request.getUsername());
        UserResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Registrasi user berhasil.", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("REST Request: POST /api/v1/auth/login untuk email: {}", request.getUserEmail());
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.ok("Login berhasil.", response));
    }
}
