package toko_online.repository.impl;

import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;
import toko_online.exception.DatabaseException;
import toko_online.model.entity.Transaction;
import toko_online.model.enums.PaymentMethod;
import toko_online.model.enums.TransactionStatus;
import toko_online.repository.TransactionRepository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class TransactionRepositoryImpl implements TransactionRepository {

    private final DataSource dataSource;

    public TransactionRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Transaction save(Transaction transaction) {
        String sql = "INSERT INTO transactions (id_transaction, user_email, total_price_amount, status, date, payment_method) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = DataSourceUtils.getConnection(dataSource);
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
        } finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }

    @Override
    public Optional<Transaction> findById(String idTransaction) {
        String sql = "SELECT * FROM transactions WHERE id_transaction = ?";
        Connection conn = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, idTransaction);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToTransaction(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Gagal mencari transaksi: " + e.getMessage(), e);
        } finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
        return Optional.empty();
    }

    @Override
    public List<Transaction> findAll() {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions ORDER BY date DESC";
        Connection conn = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToTransaction(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Gagal mengambil semua transaksi: " + e.getMessage(), e);
        } finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
        return list;
    }

    @Override
    public List<Transaction> findByUserEmail(String email) {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE user_email = ? ORDER BY date DESC";
        Connection conn = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToTransaction(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Gagal mengambil transaksi pengguna: " + e.getMessage(), e);
        } finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
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
