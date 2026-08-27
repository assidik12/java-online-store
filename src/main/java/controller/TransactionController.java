package controller;

import model.dto.request.TransactionRequest;
import model.dto.response.ApiResponse;
import model.dto.response.TransactionDetailResponse;
import model.dto.response.TransactionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.TransactionService;
import service.impl.TransactionServiceImpl;

import java.util.List;

public class TransactionController {

    private static final Logger log = LoggerFactory.getLogger(TransactionController.class);
    private final TransactionService transactionService;

    public TransactionController() {
        this.transactionService = new TransactionServiceImpl();
    }

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    public ApiResponse<TransactionResponse> buyProduct(TransactionRequest request) {
        log.info("Request buyProduct diterima di TransactionController.");
        try {
            TransactionResponse response = transactionService.createTransaction(request);
            return ApiResponse.ok("Transaksi berhasil diproses", response);
        } catch (Exception e) {
            log.error("Error pada buyProduct di TransactionController: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    public ApiResponse<List<TransactionResponse>> getTransactions(String email) {
        log.info("Request getTransactions diterima di TransactionController (filter email: '{}').", email != null ? email : "SEMUA");
        try {
            List<TransactionResponse> list = transactionService.getTransactionsByUserEmail(email);
            return ApiResponse.ok("Berhasil mengambil data transaksi", list);
        } catch (Exception e) {
            log.error("Error pada getTransactions di TransactionController: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    public ApiResponse<List<TransactionDetailResponse>> getTransactionDetails(String email) {
        log.info("Request getTransactionDetails diterima di TransactionController (filter email: '{}').", email);
        try {
            List<TransactionDetailResponse> list = transactionService.getTransactionDetails(email);
            return ApiResponse.ok("Berhasil mengambil detail transaksi", list);
        } catch (Exception e) {
            log.error("Error pada getTransactionDetails di TransactionController: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
}
