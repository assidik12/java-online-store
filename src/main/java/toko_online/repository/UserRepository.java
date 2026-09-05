package toko_online.repository;

import java.util.List;
import java.util.Optional;

import toko_online.model.entity.User;

public interface UserRepository {
    User save(User user);

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    List<User> findAll();

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}
