package toko_online.unit.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toko_online.exception.DatabaseException;
import toko_online.exception.UnauthorizedException;
import toko_online.exception.ValidationException;
import toko_online.model.dto.request.LoginRequest;
import toko_online.model.dto.request.RegisterRequest;
import toko_online.model.dto.response.LoginResponse;
import toko_online.model.dto.response.UserResponse;
import toko_online.model.entity.User;
import toko_online.model.enums.Role;
import toko_online.repository.UserRepository;
import toko_online.security.JwtService;
import toko_online.security.TokenProvider;
import toko_online.service.impl.AuthServiceImpl;
import toko_online.support.TestDataFactory;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenProvider jwtService;

    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(userRepository, jwtService);
    }

    @Test
    @DisplayName("login: null request should throw ValidationException")
    void login_nullRequest_throwsValidationException() {
        assertThatThrownBy(() -> authService.login(null))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Data login tidak boleh kosong.");
    }

    @Test
    @DisplayName("login: empty email should throw ValidationException")
    void login_emptyEmail_throwsValidationException() {
        LoginRequest req = new LoginRequest();
        req.setUserEmail("   ");
        req.setPassword("secret");

        assertThatThrownBy(() -> authService.login(req))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Email wajib diisi.");
    }

    @Test
    @DisplayName("login: empty password should throw ValidationException")
    void login_emptyPassword_throwsValidationException() {
        LoginRequest req = new LoginRequest();
        req.setUserEmail("user@example.com");
        req.setPassword("   ");

        assertThatThrownBy(() -> authService.login(req))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Password wajib diisi.");
    }

    @Test
    @DisplayName("login: unknown identifier should throw UnauthorizedException")
    void login_unknownUser_throwsUnauthorizedException() {
        LoginRequest req = TestDataFactory.validLoginRequest("unknown@example.com", "secret");
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("unknown@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(req))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Email atau password salah.");
    }

    @Test
    @DisplayName("login: wrong password should throw UnauthorizedException")
    void login_wrongPassword_throwsUnauthorizedException() {
        String rawPassword = "correctPassword123";
        String hashed = BCrypt.withDefaults().hashToString(12, rawPassword.toCharArray());

        User user = TestDataFactory.regularUser();
        user.setPassword(hashed);

        LoginRequest req = TestDataFactory.validLoginRequest(user.getEmail(), "wrongPassword");
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> authService.login(req))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Email atau password salah.");
    }

    @Test
    @DisplayName("login: valid credentials should return LoginResponse")
    void login_validCredentials_returnsLoginResponse() {
        String rawPassword = "correctPassword123";
        String hashed = BCrypt.withDefaults().hashToString(12, rawPassword.toCharArray());

        User user = TestDataFactory.regularUser();
        user.setPassword(hashed);

        LoginRequest req = TestDataFactory.validLoginRequest(user.getEmail(), rawPassword);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(jwtService.generateAccessToken(user)).thenReturn("mock.access.token");
        when(jwtService.generateRefreshToken(user)).thenReturn("mock.refresh.token");
        when(jwtService.getAccessTokenExpirationSeconds()).thenReturn(300L);

        LoginResponse response = authService.login(req);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("mock.access.token");
        assertThat(response.getRefreshToken()).isEqualTo("mock.refresh.token");
        assertThat(response.getUsername()).isEqualTo(user.getUsername());
        assertThat(response.getUserEmail()).isEqualTo(user.getEmail());
    }

    @Test
    @DisplayName("register: null request should throw ValidationException")
    void register_nullRequest_throwsValidationException() {
        assertThatThrownBy(() -> authService.register(null))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("register: existing username should throw ValidationException")
    void register_existingUsername_throwsValidationException() {
        RegisterRequest req = TestDataFactory.validRegisterRequest("existing_user", "new@example.com");
        when(userRepository.existsByUsername("existing_user")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(req))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("sudah terdaftar.");
    }

    @Test
    @DisplayName("register: existing email should throw ValidationException")
    void register_existingEmail_throwsValidationException() {
        RegisterRequest req = TestDataFactory.validRegisterRequest("new_user", "existing@example.com");
        when(userRepository.existsByUsername("new_user")).thenReturn(false);
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(req))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("sudah terdaftar.");
    }

    @Test
    @DisplayName("register: valid request should save and return UserResponse with Role.USER")
    void register_validRequest_success() {
        RegisterRequest req = TestDataFactory.validRegisterRequest("fresh_user", "fresh@example.com");
        when(userRepository.existsByUsername("fresh_user")).thenReturn(false);
        when(userRepository.existsByEmail("fresh@example.com")).thenReturn(false);

        User savedMock = new User(req.getUsername(), "hashed", req.getEmail(), Role.USER, req.getPhoneNumber(), req.getAddress(), req.getPosCode());
        savedMock.setId(101L);
        when(userRepository.save(any(User.class))).thenReturn(savedMock);

        UserResponse response = authService.register(req);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(101L);
        assertThat(response.getUsername()).isEqualTo("fresh_user");
        assertThat(response.getRole()).isEqualTo(Role.USER);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("register: database duplicate key race condition caught and mapped")
    void register_duplicateKeyRaceCondition_throwsValidationException() {
        RegisterRequest req = TestDataFactory.validRegisterRequest("race_user", "race@example.com");
        when(userRepository.existsByUsername("race_user")).thenReturn(false);
        when(userRepository.existsByEmail("race@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenThrow(new DatabaseException("Duplicate entry 'race@example.com' for key 'users.email'"));

        assertThatThrownBy(() -> authService.register(req))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Username atau email sudah terdaftar.");
    }

    @Test
    @DisplayName("refresh: null or blank token should throw ValidationException")
    void refresh_blankToken_throwsValidationException() {
        assertThatThrownBy(() -> authService.refresh("  "))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("refresh: invalid token should throw UnauthorizedException")
    void refresh_invalidToken_throwsUnauthorizedException() {
        when(jwtService.isTokenValid("invalid.token")).thenReturn(false);

        assertThatThrownBy(() -> authService.refresh("invalid.token"))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    @DisplayName("refresh: valid token should return new LoginResponse")
    void refresh_validToken_success() {
        User user = TestDataFactory.regularUser();
        when(jwtService.isTokenValid("valid.refresh.token")).thenReturn(true);
        when(jwtService.extractUserId("valid.refresh.token")).thenReturn(user.getId());
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(jwtService.generateAccessToken(user)).thenReturn("new.access.token");
        when(jwtService.generateRefreshToken(user)).thenReturn("new.refresh.token");

        LoginResponse res = authService.refresh("valid.refresh.token");

        assertThat(res).isNotNull();
        assertThat(res.getAccessToken()).isEqualTo("new.access.token");
        assertThat(res.getRefreshToken()).isEqualTo("new.refresh.token");
    }
}
