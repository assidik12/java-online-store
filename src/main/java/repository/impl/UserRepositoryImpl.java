package repository.impl;

import config.DatabaseConfig;
import exception.DatabaseException;
import model.entity.User;
import model.enums.Role;
import repository.UserRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepositoryImpl implements UserRepository {

    @Override
    public User save(User user) {
        String sql = "INSERT INTO user (username, password, email, role, phone_number, address, pos_code) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getRole() != null ? user.getRole().name().toLowerCase() : "user");
            stmt.setString(5, user.getPhoneNumber());
            stmt.setString(6, user.getAddress());
            stmt.setString(7, user.getPosCode());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getLong(1));
                    }
                }
            }
            return user;
        } catch (SQLException e) {
            throw new DatabaseException("Gagal menyimpan data user: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        String sql = "SELECT * FROM user WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Gagal mencari user berdasarkan id: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM user WHERE email = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Gagal mencari user berdasarkan email: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM user WHERE username = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Gagal mencari user berdasarkan username: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM user";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Gagal mengambil semua user: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public boolean existsByEmail(String email) {
        String sql = "SELECT 1 FROM user WHERE email = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Gagal memeriksa email: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existsByUsername(String username) {
        String sql = "SELECT 1 FROM user WHERE username = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Gagal memeriksa username: " + e.getMessage(), e);
        }
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        try {
            user.setId(rs.getLong("id"));
        } catch (SQLException ignored) {
            // Kolom id mungkin tidak ada di skema lama jika belum auto increment
        }
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setEmail(rs.getString("email"));
        user.setRole(Role.fromString(rs.getString("role")));
        user.setPhoneNumber(rs.getString("phone_number"));
        user.setAddress(rs.getString("address"));
        user.setPosCode(rs.getString("pos_code"));
        return user;
    }
}
