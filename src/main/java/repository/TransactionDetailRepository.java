package repository;

import model.entity.TransactionDetail;

import java.sql.Connection;
import java.util.List;

public interface TransactionDetailRepository {
    TransactionDetail save(TransactionDetail detail);
    TransactionDetail save(Connection conn, TransactionDetail detail);
    List<TransactionDetail> findByTransactionId(String transactionId);
    List<TransactionDetail> findByTransactionId(Connection conn, String transactionId);
    List<TransactionDetail> findByUserEmail(String email);
}
