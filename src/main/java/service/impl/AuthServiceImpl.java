package service.impl;

import at.favre.lib.crypto.bcrypt.BCrypt;
import exception.UnauthorizedException;
import exception.ValidationException;
import model.dto.request.RegisterRequest;
import model.dto.request.LoginRequest;
import model.dto.response.LoginResponse;
import model.dto.response.UserResponse;
import model.entity.User;
import model.enums.Role;
import repository.UserRepository;
import repository.impl.UserRepositoryImpl;
import service.AuthService;

public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    public AuthServiceImpl() {
        this.userRepository = new UserRepositoryImpl();
    }

    public AuthServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        if (request == null) {
            throw new ValidationException("Data login tidak boleh kosong.");
        }
        if (request.getUserEmail() == null || request.getUserEmail().trim().isEmpty()) {
            throw new ValidationException("Email wajib diisi.");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new ValidationException("Password wajib diisi.");
        }

        User findUser = userRepository.findByEmail(request.getUserEmail())
                .orElseThrow(() -> new UnauthorizedException("Email atau password salah."));

        BCrypt.Result result = BCrypt.verifyer().verify(request.getPassword().toCharArray(),
                findUser.getPassword().toCharArray());

        if (!result.verified) {
            throw new UnauthorizedException("Email atau password salah.");
        }

        return new LoginResponse(
                findUser.getId(),
                findUser.getUsername(),
                findUser.getEmail(),
                java.util.UUID.randomUUID().toString());
    }

    @Override
    public UserResponse register(RegisterRequest request) {
        if (request == null) {
            throw new ValidationException("Data registrasi tidak boleh kosong.");
        }
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new ValidationException("Username wajib diisi.");
        }
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new ValidationException("Email wajib diisi.");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new ValidationException("Password wajib diisi.");
        }

        if (userRepository.existsByUsername(request.getUsername().trim())) {
            throw new ValidationException("Username '" + request.getUsername() + "' sudah terdaftar.");
        }
        if (userRepository.existsByEmail(request.getEmail().trim())) {
            throw new ValidationException("Email '" + request.getEmail() + "' sudah terdaftar.");
        }

        // Enkripsi password menggunakan BCrypt
        String hashedPassword = BCrypt.withDefaults().hashToString(12, request.getPassword().toCharArray());

        User user = new User(
                request.getUsername().trim(),
                hashedPassword,
                request.getEmail().trim(),
                Role.USER,
                request.getPhoneNumber(),
                request.getAddress(),
                request.getPosCode());

        User savedUser = userRepository.save(user);

        return new UserResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getRole(),
                savedUser.getPhoneNumber(),
                savedUser.getAddress(),
                savedUser.getPosCode());
    }
}
