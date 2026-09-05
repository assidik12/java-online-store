package toko_online.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import toko_online.model.dto.response.ApiResponse;
import toko_online.model.dto.response.UserResponse;
import toko_online.model.entity.User;
import toko_online.repository.UserRepository;
import toko_online.security.AppUserPrincipal;
import toko_online.security.CurrentUser;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "Profil user saat ini dan listing user (ADMIN).")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    @Operation(summary = "Profil user yang sedang login")
    public ResponseEntity<ApiResponse<UserResponse>> me() {
        AppUserPrincipal principal = CurrentUser.require();
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new toko_online.exception.ResourceNotFoundException("User tidak ditemukan."));
        return ResponseEntity.ok(ApiResponse.ok("Profil berhasil diambil.", toResponse(user)));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "List semua user (ADMIN)")
    public ResponseEntity<ApiResponse<List<UserResponse>>> all() {
        List<UserResponse> users = userRepository.findAll().stream().map(this::toResponse).toList();
        return ResponseEntity.ok(ApiResponse.ok("Daftar user berhasil diambil.", users));
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getPhoneNumber(),
                user.getAddress(),
                user.getPosCode());
    }
}
