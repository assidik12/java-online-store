package service;

import model.dto.request.TransactionRequest;
import model.dto.response.TransactionDetailResponse;
import model.dto.response.TransactionResponse;

import java.util.List;

public interface TransactionService {
    TransactionResponse createTransaction(TransactionRequest request);
    List<TransactionResponse> getTransactionsByUserEmail(String email);
    List<TransactionDetailResponse> getTransactionDetails(String email);
}
