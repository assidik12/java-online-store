package repository.impl;

import config.DatabaseConfig;
import exception.DatabaseException;
import model.entity.Product;
import repository.ProductRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductRepositoryImpl implements ProductRepository {

    @Override
    public Product save(Product product) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            return save(conn, product);
        } catch (SQLException e) {
            throw new DatabaseException("Gagal menyimpan produk: " + e.getMessage(), e);
        }
    }

    @Override
    public Product save(Connection conn, Product product) {
        String sql = "INSERT INTO product (name, price, stock) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, product.getName());
            stmt.setDouble(2, product.getPrice());
            stmt.setInt(3, product.getStock());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        product.setId(keys.getInt(1));
                    }
                }
            }
            return product;
        } catch (SQLException e) {
            throw new DatabaseException("Gagal menyimpan data produk: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Product> findById(Integer id) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            return findById(conn, id);
        } catch (SQLException e) {
            throw new DatabaseException("Gagal mencari produk berdasarkan ID: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Product> findById(Connection conn, Integer id) {
        String sql = "SELECT * FROM product WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToProduct(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Gagal mengambil data produk: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<Product> findAll() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM product";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Gagal mengambil daftar produk: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public boolean updateStock(Integer id, Integer newStock) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            return updateStock(conn, id, newStock);
        } catch (SQLException e) {
            throw new DatabaseException("Gagal mengupdate stok produk: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updateStock(Connection conn, Integer id, Integer newStock) {
        String sql = "UPDATE product SET stock = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, newStock);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Gagal mengupdate stok produk di database: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM product WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Gagal menghapus produk: " + e.getMessage(), e);
        }
    }

    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        return new Product(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getDouble("price"),
                rs.getInt("stock")
        );
    }
}
