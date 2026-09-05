package toko_online.slice.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import toko_online.model.entity.User;
import toko_online.model.enums.Role;
import toko_online.repository.UserRepository;
import toko_online.repository.impl.UserRepositoryImpl;
import toko_online.support.TestDataFactory;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(UserRepositoryImpl.class)
class UserRepositoryIT {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanUp() {
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
        jdbcTemplate.execute("TRUNCATE TABLE users");
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
    }

    @Test
    @DisplayName("save and findById: round-trip persistance")
    void saveAndFindById_success() {
        User user = TestDataFactory.createUser("bob", "bob@example.com", Role.USER);
        User saved = userRepository.save(user);

        assertThat(saved.getId()).isNotNull();

        Optional<User> found = userRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("bob");
        assertThat(found.get().getEmail()).isEqualTo("bob@example.com");
    }

    @Test
    @DisplayName("existsByEmail and existsByUsername: returns expected booleans")
    void existsQueries_workAsExpected() {
        User user = TestDataFactory.createUser("dave", "dave@example.com", Role.USER);
        userRepository.save(user);

        assertThat(userRepository.existsByEmail("dave@example.com")).isTrue();
        assertThat(userRepository.existsByUsername("dave")).isTrue();
        assertThat(userRepository.existsByEmail("other@example.com")).isFalse();
        assertThat(userRepository.existsByUsername("other")).isFalse();
    }
}
