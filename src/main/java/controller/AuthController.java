package controller;

import model.dto.request.LoginRequest;
import model.dto.request.RegisterRequest;
import model.dto.response.ApiResponse;
import model.dto.response.LoginResponse;
import model.dto.response.UserResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.AuthService;
import service.impl.AuthServiceImpl;

public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    public AuthController() {
        this.authService = new AuthServiceImpl();
    }

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    public ApiResponse<UserResponse> register(RegisterRequest request) {
        log.info("Request registrasi user diterima di AuthController.");
        try {
            UserResponse response = authService.register(request);
            return ApiResponse.ok("Registrasi user berhasil.", response);
        } catch (Exception e) {
            log.error("Error pada AuthController saat registrasi: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    public ApiResponse<LoginResponse> login(LoginRequest request) {
        log.info("Request login diterima di AuthController.");
        try {
            LoginResponse response = authService.login(request);
            return ApiResponse.ok("Login berhasil.", response);
        } catch (Exception e) {
            log.error("Error pada AuthController saat login: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
}
