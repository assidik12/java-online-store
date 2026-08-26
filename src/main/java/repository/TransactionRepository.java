package repository;

import model.entity.Transaction;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository {
    Transaction save(Transaction transaction);
    Transaction save(Connection conn, Transaction transaction);
    Optional<Transaction> findById(String idTransaction);
    List<Transaction> findAll();
    List<Transaction> findByUserEmail(String email);
}
