package toko_online.service;

import java.util.List;

import toko_online.model.dto.request.TransactionRequest;
import toko_online.model.dto.response.TransactionDetailResponse;
import toko_online.model.dto.response.TransactionResponse;

public interface TransactionService {
    TransactionResponse createTransaction(TransactionRequest request);

    List<TransactionResponse> getTransactionsByUserEmail(String email);

    List<TransactionDetailResponse> getTransactionDetails(String email);
}
