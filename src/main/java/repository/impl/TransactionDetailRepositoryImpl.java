package repository.impl;

import config.DatabaseConfig;
import exception.DatabaseException;
import model.entity.TransactionDetail;
import repository.TransactionDetailRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TransactionDetailRepositoryImpl implements TransactionDetailRepository {

    @Override
    public TransactionDetail save(TransactionDetail detail) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            return save(conn, detail);
        } catch (SQLException e) {
            throw new DatabaseException("Gagal menyimpan detail transaksi: " + e.getMessage(), e);
        }
    }

    @Override
    public TransactionDetail save(Connection conn, TransactionDetail detail) {
        String sql = "INSERT INTO transactions_details (transaction_id, product_id, total_price_product, quantity) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, detail.getTransactionId());
            stmt.setInt(2, detail.getProductId());
            stmt.setInt(3, detail.getTotalPriceProduct());
            stmt.setInt(4, detail.getQuantity());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        detail.setId(keys.getInt(1));
                    }
                }
            }
            return detail;
        } catch (SQLException e) {
            throw new DatabaseException("Gagal menyimpan detail transaksi ke database: " + e.getMessage(), e);
        }
    }

    @Override
    public List<TransactionDetail> findByTransactionId(String transactionId) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            return findByTransactionId(conn, transactionId);
        } catch (SQLException e) {
            throw new DatabaseException("Gagal mengambil detail transaksi: " + e.getMessage(), e);
        }
    }

    @Override
    public List<TransactionDetail> findByTransactionId(Connection conn, String transactionId) {
        List<TransactionDetail> list = new ArrayList<>();
        String sql = "SELECT td.*, p.name AS product_name FROM transactions_details td " +
                     "LEFT JOIN product p ON td.product_id = p.id " +
                     "WHERE td.transaction_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, transactionId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToDetail(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Gagal mengambil detail transaksi: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<TransactionDetail> findByUserEmail(String email) {
        List<TransactionDetail> list = new ArrayList<>();
        String sql = "SELECT td.*, p.name AS product_name FROM transactions_details td " +
                     "INNER JOIN transactions t ON td.transaction_id = t.id_transaction " +
                     "LEFT JOIN product p ON td.product_id = p.id " +
                     (email != null && !email.trim().isEmpty() ? "WHERE t.user_email = ? " : "") +
                     "ORDER BY t.date DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (email != null && !email.trim().isEmpty()) {
                stmt.setString(1, email);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToDetail(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Gagal mengambil detail transaksi berdasarkan email: " + e.getMessage(), e);
        }
        return list;
    }

    private TransactionDetail mapResultSetToDetail(ResultSet rs) throws SQLException {
        TransactionDetail detail = new TransactionDetail();
        try {
            detail.setId(rs.getInt("id"));
        } catch (SQLException ignored) {
        }
        detail.setTransactionId(rs.getString("transaction_id"));
        detail.setProductId(rs.getInt("product_id"));
        detail.setTotalPriceProduct(rs.getInt("total_price_product"));
        detail.setQuantity(rs.getInt("quantity"));
        try {
            detail.setProductName(rs.getString("product_name"));
        } catch (SQLException ignored) {
        }
        return detail;
    }
}
