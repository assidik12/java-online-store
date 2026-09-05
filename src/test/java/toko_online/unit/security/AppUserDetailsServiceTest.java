package toko_online.unit.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import toko_online.model.entity.User;
import toko_online.model.enums.Role;
import toko_online.repository.UserRepository;
import toko_online.security.AppUserDetailsService;
import toko_online.support.TestDataFactory;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AppUserDetailsService userDetailsService;

    @Test
    @DisplayName("loadUserByUsername: existing email returns UserDetails with authorities")
    void loadUserByUsername_existingEmail_returnsUserDetails() {
        User user = TestDataFactory.createUser("alice", "alice@example.com", Role.ADMIN);
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));

        UserDetails details = userDetailsService.loadUserByUsername("alice@example.com");

        assertThat(details).isNotNull();
        assertThat(details.getUsername()).isEqualTo("alice");
        assertThat(details.getAuthorities()).anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    @Test
    @DisplayName("loadUserByUsername: unknown identifier throws UsernameNotFoundException")
    void loadUserByUsername_unknown_throwsUsernameNotFoundException() {
        when(userRepository.findByEmail("unknown")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("unknown"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User tidak ditemukan");
    }
}
