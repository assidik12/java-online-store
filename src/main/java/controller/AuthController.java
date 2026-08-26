package controller;

import model.dto.request.LoginRequest;
import model.dto.request.RegisterRequest;
import model.dto.response.ApiResponse;
import model.dto.response.LoginResponse;
import model.dto.response.UserResponse;
import service.AuthService;
import service.impl.AuthServiceImpl;

public class AuthController {

    private final AuthService authService;

    public AuthController() {
        this.authService = new AuthServiceImpl();
    }

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    public ApiResponse<UserResponse> register(RegisterRequest request) {
        try {
            UserResponse response = authService.register(request);
            return ApiResponse.ok("Registrasi user berhasil.", response);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    public ApiResponse<LoginResponse> login(LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return ApiResponse.ok("Login berhasil.", response);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}
