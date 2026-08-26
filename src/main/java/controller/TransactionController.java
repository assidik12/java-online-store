package controller;

import model.dto.request.TransactionRequest;
import model.dto.response.ApiResponse;
import model.dto.response.TransactionDetailResponse;
import model.dto.response.TransactionResponse;
import service.TransactionService;
import service.impl.TransactionServiceImpl;

import java.util.List;

public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController() {
        this.transactionService = new TransactionServiceImpl();
    }

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    public ApiResponse<TransactionResponse> buyProduct(TransactionRequest request) {
        try {
            TransactionResponse response = transactionService.createTransaction(request);
            return ApiResponse.ok("Transaksi berhasil diproses", response);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    public ApiResponse<List<TransactionResponse>> getTransactions(String email) {
        try {
            List<TransactionResponse> list = transactionService.getTransactionsByUserEmail(email);
            return ApiResponse.ok("Berhasil mengambil data transaksi", list);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    public ApiResponse<List<TransactionDetailResponse>> getTransactionDetails(String email) {
        try {
            List<TransactionDetailResponse> list = transactionService.getTransactionDetails(email);
            return ApiResponse.ok("Berhasil mengambil detail transaksi", list);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}
