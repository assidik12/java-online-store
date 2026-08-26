package service;

import model.dto.request.RegisterRequest;
import model.dto.request.LoginRequest;
import model.dto.response.LoginResponse;
import model.dto.response.UserResponse;

public interface AuthService {
    UserResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);
}
