package repository.impl;

import config.DatabaseConfig;
import exception.DatabaseException;
import model.entity.Transaction;
import model.enums.PaymentMethod;
import model.enums.TransactionStatus;
import repository.TransactionRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TransactionRepositoryImpl implements TransactionRepository {

    @Override
    public Transaction save(Transaction transaction) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            return save(conn, transaction);
        } catch (SQLException e) {
            throw new DatabaseException("Gagal menyimpan transaksi: " + e.getMessage(), e);
        }
    }

    @Override
    public Transaction save(Connection conn, Transaction transaction) {
        String sql = "INSERT INTO transactions (id_transaction, user_email, total_price_amount, status, date, payment_method) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, transaction.getIdTransaction());
            stmt.setString(2, transaction.getUserEmail());
            stmt.setInt(3, transaction.getTotalPriceAmount() != null ? transaction.getTotalPriceAmount() : 0);
            stmt.setBoolean(4, transaction.getStatus() == TransactionStatus.PAID);
            stmt.setDate(5, new java.sql.Date(transaction.getDate() != null ? transaction.getDate().getTime() : System.currentTimeMillis()));
            stmt.setString(6, transaction.getPaymentMethod() != null ? transaction.getPaymentMethod().getDisplayName() : "Cash");

            stmt.executeUpdate();
            return transaction;
        } catch (SQLException e) {
            throw new DatabaseException("Gagal menyimpan transaksi ke database: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Transaction> findById(String idTransaction) {
        String sql = "SELECT * FROM transactions WHERE id_transaction = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, idTransaction);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToTransaction(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Gagal mencari transaksi: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<Transaction> findAll() {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions ORDER BY date DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToTransaction(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Gagal mengambil semua transaksi: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<Transaction> findByUserEmail(String email) {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE user_email = ? ORDER BY date DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToTransaction(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Gagal mengambil transaksi pengguna: " + e.getMessage(), e);
        }
        return list;
    }

    private Transaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        return new Transaction(
                rs.getString("id_transaction"),
                rs.getString("user_email"),
                rs.getInt("total_price_amount"),
                rs.getBoolean("status") ? TransactionStatus.PAID : TransactionStatus.PENDING,
                rs.getDate("date"),
                PaymentMethod.fromString(rs.getString("payment_method"))
        );
    }
}
