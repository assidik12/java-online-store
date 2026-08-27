package service.impl;

import at.favre.lib.crypto.bcrypt.BCrypt;
import exception.UnauthorizedException;
import exception.ValidationException;
import model.dto.request.LoginRequest;
import model.dto.request.RegisterRequest;
import model.dto.response.LoginResponse;
import model.dto.response.UserResponse;
import model.entity.User;
import model.enums.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.UserRepository;
import repository.impl.UserRepositoryImpl;
import service.AuthService;

import java.util.UUID;

public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);
    private final UserRepository userRepository;

    public AuthServiceImpl() {
        this.userRepository = new UserRepositoryImpl();
    }

    public AuthServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        log.info("Menerima permintaan login untuk identifier: {}", request != null ? request.getUserEmail() : "null");

        if (request == null) {
            log.warn("Login gagal: Request data kosong.");
            throw new ValidationException("Data login tidak boleh kosong.");
        }
        if (request.getUserEmail() == null || request.getUserEmail().trim().isEmpty()) {
            log.warn("Login gagal: Email belum diisi.");
            throw new ValidationException("Email wajib diisi.");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            log.warn("Login gagal: Password belum diisi.");
            throw new ValidationException("Password wajib diisi.");
        }

        User findUser = userRepository.findByEmail(request.getUserEmail().trim())
                .orElseThrow(() -> {
                    log.warn("Login gagal: User dengan email '{}' tidak ditemukan.", request.getUserEmail());
                    return new UnauthorizedException("Email atau password salah.");
                });

        BCrypt.Result result = BCrypt.verifyer().verify(
                request.getPassword().toCharArray(),
                findUser.getPassword().toCharArray()
        );

        if (!result.verified) {
            log.warn("Login gagal: Password tidak cocok untuk user '{}'.", findUser.getUsername());
            throw new UnauthorizedException("Email atau password salah.");
        }

        String token = UUID.randomUUID().toString();
        log.info("Login berhasil untuk user: {} (ID: {})", findUser.getUsername(), findUser.getId());

        return new LoginResponse(
                findUser.getId(),
                findUser.getUsername(),
                findUser.getEmail(),
                token
        );
    }

    @Override
    public UserResponse register(RegisterRequest request) {
        log.info("Menerima permintaan registrasi user baru: {}", request != null ? request.getUsername() : "null");

        if (request == null) {
            log.warn("Registrasi gagal: Request data kosong.");
            throw new ValidationException("Data registrasi tidak boleh kosong.");
        }
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            log.warn("Registrasi gagal: Username belum diisi.");
            throw new ValidationException("Username wajib diisi.");
        }
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            log.warn("Registrasi gagal: Email belum diisi.");
            throw new ValidationException("Email wajib diisi.");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            log.warn("Registrasi gagal: Password belum diisi.");
            throw new ValidationException("Password wajib diisi.");
        }

        if (userRepository.existsByUsername(request.getUsername().trim())) {
            log.warn("Registrasi gagal: Username '{}' sudah terdaftar.", request.getUsername());
            throw new ValidationException("Username '" + request.getUsername() + "' sudah terdaftar.");
        }
        if (userRepository.existsByEmail(request.getEmail().trim())) {
            log.warn("Registrasi gagal: Email '{}' sudah terdaftar.", request.getEmail());
            throw new ValidationException("Email '" + request.getEmail() + "' sudah terdaftar.");
        }

        log.debug("Meng-hash password untuk user: {}", request.getUsername());
        String hashedPassword = BCrypt.withDefaults().hashToString(12, request.getPassword().toCharArray());

        User user = new User(
                request.getUsername().trim(),
                hashedPassword,
                request.getEmail().trim(),
                Role.USER,
                request.getPhoneNumber(),
                request.getAddress(),
                request.getPosCode()
        );

        User savedUser = userRepository.save(user);
        log.info("User baru berhasil didaftarkan: {} (ID: {})", savedUser.getUsername(), savedUser.getId());

        return new UserResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getRole(),
                savedUser.getPhoneNumber(),
                savedUser.getAddress(),
                savedUser.getPosCode()
        );
    }
}
