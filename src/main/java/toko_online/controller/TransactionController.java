package toko_online.controller;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import toko_online.model.dto.request.TransactionRequest;
import toko_online.model.dto.response.ApiResponse;
import toko_online.model.dto.response.TransactionDetailResponse;
import toko_online.model.dto.response.TransactionResponse;
import toko_online.service.TransactionService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private static final Logger log = LoggerFactory.getLogger(TransactionController.class);
    private final TransactionService transactionService;

    // Constructor Injection: Spring otomatis meng-inject bean TransactionService
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TransactionResponse>> buyProduct(@Valid @RequestBody TransactionRequest request) {
        log.info("REST Request: POST /api/v1/transactions untuk produk ID: {}", request.getProductId());
        TransactionResponse response = transactionService.createTransaction(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Transaksi berhasil diproses", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getTransactions(
            @RequestParam(required = false) String email) {
        log.info("REST Request: GET /api/v1/transactions (filter email: '{}')", email != null ? email : "SEMUA");
        List<TransactionResponse> list = transactionService.getTransactionsByUserEmail(email);
        return ResponseEntity.ok(ApiResponse.ok("Berhasil mengambil data transaksi", list));
    }

    @GetMapping("/details")
    public ResponseEntity<ApiResponse<List<TransactionDetailResponse>>> getTransactionDetails(
            @RequestParam(required = false) String email) {
        log.info("REST Request: GET /api/v1/transactions/details (filter email: '{}')", email);
        List<TransactionDetailResponse> list = transactionService.getTransactionDetails(email);
        return ResponseEntity.ok(ApiResponse.ok("Berhasil mengambil detail transaksi", list));
    }
}
