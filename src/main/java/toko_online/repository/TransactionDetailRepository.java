package toko_online.repository;

import toko_online.model.entity.TransactionDetail;

import java.util.List;

public interface TransactionDetailRepository {
    TransactionDetail save(TransactionDetail detail);

    List<TransactionDetail> findByTransactionId(String transactionId);

    List<TransactionDetail> findByUserEmail(String email);
}
