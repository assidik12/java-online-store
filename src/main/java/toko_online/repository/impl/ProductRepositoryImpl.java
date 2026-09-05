package toko_online.repository.impl;

import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;
import toko_online.exception.DatabaseException;
import toko_online.model.entity.Product;
import toko_online.repository.ProductRepository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ProductRepositoryImpl implements ProductRepository {

    private final DataSource dataSource;

    public ProductRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Product save(Product product) {
        String sql = "INSERT INTO product (name, price, stock) VALUES (?, ?, ?)";
        Connection conn = DataSourceUtils.getConnection(dataSource);
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
        } finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }

    @Override
    public Product update(Product product) {
        String sql = "UPDATE product SET name = ?, price = ?, stock = ? WHERE id = ?";
        Connection conn = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, product.getName());
            stmt.setDouble(2, product.getPrice());
            stmt.setInt(3, product.getStock());
            stmt.setInt(4, product.getId());

            stmt.executeUpdate();
            return product;
        } catch (SQLException e) {
            throw new DatabaseException("Gagal memperbarui data produk: " + e.getMessage(), e);
        } finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }

    @Override
    public Optional<Product> findById(Integer id) {
        String sql = "SELECT * FROM product WHERE id = ?";
        Connection conn = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToProduct(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Gagal mencari produk berdasarkan ID: " + e.getMessage(), e);
        } finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Product> findByIdForUpdate(Integer id) {
        String sql = "SELECT * FROM product WHERE id = ? FOR UPDATE";
        Connection conn = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToProduct(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Gagal mengunci produk untuk update: " + e.getMessage(), e);
        } finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
        return Optional.empty();
    }

    @Override
    public List<Product> findAll() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM product";
        Connection conn = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Gagal mengambil daftar produk: " + e.getMessage(), e);
        } finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
        return list;
    }

    @Override
    public boolean updateStock(Integer id, Integer newStock) {
        String sql = "UPDATE product SET stock = ? WHERE id = ?";
        Connection conn = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, newStock);
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Gagal mengupdate stok produk di database: " + e.getMessage(), e);
        } finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }

    @Override
    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM product WHERE id = ?";
        Connection conn = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Gagal menghapus produk: " + e.getMessage(), e);
        } finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
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
