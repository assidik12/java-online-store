package toko_online.repository;

import toko_online.model.entity.Transaction;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository {
    Transaction save(Transaction transaction);

    Optional<Transaction> findById(String idTransaction);

    List<Transaction> findAll();

    List<Transaction> findByUserEmail(String email);
}
