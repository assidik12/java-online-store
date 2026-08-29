package toko_online.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import toko_online.exception.InsufficientStockException;
import toko_online.exception.ResourceNotFoundException;
import toko_online.exception.ValidationException;
import toko_online.model.dto.request.TransactionRequest;
import toko_online.model.dto.response.TransactionDetailResponse;
import toko_online.model.dto.response.TransactionResponse;
import toko_online.model.entity.Product;
import toko_online.model.entity.Transaction;
import toko_online.model.entity.TransactionDetail;
import toko_online.model.enums.PaymentMethod;
import toko_online.model.enums.TransactionStatus;
import toko_online.repository.ProductRepository;
import toko_online.repository.TransactionDetailRepository;
import toko_online.repository.TransactionRepository;
import toko_online.service.TransactionService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionService {

    private static final Logger log = LoggerFactory.getLogger(TransactionServiceImpl.class);
    private final ProductRepository productRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionDetailRepository transactionDetailRepository;

    // Constructor Injection: Spring otomatis meng-inject seluruh dependensi repository
    public TransactionServiceImpl(ProductRepository productRepository,
                                  TransactionRepository transactionRepository,
                                  TransactionDetailRepository transactionDetailRepository) {
        this.productRepository = productRepository;
        this.transactionRepository = transactionRepository;
        this.transactionDetailRepository = transactionDetailRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TransactionResponse createTransaction(TransactionRequest request) {
        log.info("Menerima permohonan transaksi pembelian untuk produk ID: {} dari pembeli: {}",
                request != null ? request.getProductId() : "null",
                request != null ? request.getUserEmail() : "null");

        if (request == null) {
            log.warn("Transaksi gagal: Request data kosong.");
            throw new ValidationException("Data transaksi tidak boleh kosong.");
        }
        if (request.getProductId() == null || request.getProductId() <= 0) {
            log.warn("Transaksi gagal: ID Produk tidak valid.");
            throw new ValidationException("ID Produk harus valid dan lebih dari 0.");
        }
        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            log.warn("Transaksi gagal: Kuantitas tidak valid: {}", request.getQuantity());
            throw new ValidationException("Kuantitas harus lebih dari 0.");
        }
        if (request.getUserEmail() == null || request.getUserEmail().trim().isEmpty()) {
            log.warn("Transaksi gagal: Email pembeli kosong.");
            throw new ValidationException("Email pembeli wajib diisi.");
        }
        if (request.getPaidAmount() == null || request.getPaidAmount() <= 0) {
            log.warn("Transaksi gagal: Jumlah pembayaran tidak valid: {}", request.getPaidAmount());
            throw new ValidationException("Jumlah pembayaran harus lebih dari 0.");
        }

        PaymentMethod paymentMethod = request.getPaymentMethod() != null ? request.getPaymentMethod() : PaymentMethod.CASH;

        // 1. Ambil & Kunci Produk (SELECT ... FOR UPDATE untuk mencegah race condition stok)
        Product product = productRepository.findByIdForUpdate(request.getProductId())
                .orElseThrow(() -> {
                    log.warn("Transaksi gagal: Produk ID {} tidak ditemukan.", request.getProductId());
                    return new ResourceNotFoundException("Produk dengan ID " + request.getProductId() + " tidak ditemukan.");
                });

        // 2. Validasi Stok
        if (product.getStock() < request.getQuantity()) {
            log.warn("Transaksi gagal: Stok tidak cukup untuk produk {} (stok: {}, diminta: {})",
                    product.getName(), product.getStock(), request.getQuantity());
            throw new InsufficientStockException("Stok tidak mencukupi! Stok saat ini: " + product.getStock() + ", diminta: " + request.getQuantity());
        }

        // 3. Hitung Total Harga & Kembalian
        int totalPriceProduct = (int) (product.getPrice() * request.getQuantity());
        if (request.getPaidAmount() < totalPriceProduct) {
            log.warn("Transaksi gagal: Uang dibayarkan ({}) kurang dari total tagihan ({})",
                    request.getPaidAmount(), totalPriceProduct);
            throw new ValidationException("Jumlah uang dibayarkan (" + request.getPaidAmount() + ") kurang dari total harga (" + totalPriceProduct + ").");
        }
        int changeAmount = request.getPaidAmount() - totalPriceProduct;

        // 4. Update Stok Produk
        int newStock = product.getStock() - request.getQuantity();
        log.debug("Mengurangi stok produk ID {} dari {} menjadi {}", product.getId(), product.getStock(), newStock);
        productRepository.updateStock(product.getId(), newStock);

        // 5. Simpan Header Transaksi
        String transactionId = UUID.randomUUID().toString();
        Date now = new Date();
        Transaction transaction = new Transaction(
                transactionId,
                request.getUserEmail().trim(),
                totalPriceProduct,
                TransactionStatus.PAID,
                now,
                paymentMethod
        );
        transactionRepository.save(transaction);

        // 6. Simpan Detail Transaksi
        TransactionDetail detail = new TransactionDetail(
                transactionId,
                product.getId(),
                request.getQuantity(),
                totalPriceProduct
        );
        transactionDetailRepository.save(detail);

        log.info("Transaksi berhasil diproses! ID Transaksi: {}, Total: Rp. {}, Kembalian: Rp. {}",
                transactionId, totalPriceProduct, changeAmount);

        // 7. Bangun Response
        TransactionDetailResponse detailResponse = new TransactionDetailResponse(
                detail.getId(),
                transactionId,
                product.getId(),
                product.getName(),
                request.getQuantity(),
                totalPriceProduct
        );

        List<TransactionDetailResponse> details = new ArrayList<>();
        details.add(detailResponse);

        return new TransactionResponse(
                transactionId,
                request.getUserEmail().trim(),
                totalPriceProduct,
                request.getPaidAmount(),
                changeAmount,
                TransactionStatus.PAID,
                now,
                paymentMethod,
                details
        );
    }

    @Override
    public List<TransactionResponse> getTransactionsByUserEmail(String email) {
        log.info("Mengambil riwayat transaksi untuk filter email: '{}'", email != null ? email : "SEMUA");
        List<Transaction> transactions = (email == null || email.trim().isEmpty())
                ? transactionRepository.findAll()
                : transactionRepository.findByUserEmail(email.trim());

        return transactions.stream().map(t -> {
            List<TransactionDetailResponse> detailResponses = transactionDetailRepository.findByTransactionId(t.getIdTransaction())
                    .stream()
                    .map(d -> new TransactionDetailResponse(
                            d.getId(),
                            d.getTransactionId(),
                            d.getProductId(),
                            d.getProductName(),
                            d.getQuantity(),
                            d.getTotalPriceProduct()
                    ))
                    .collect(Collectors.toList());

            return new TransactionResponse(
                    t.getIdTransaction(),
                    t.getUserEmail(),
                    t.getTotalPriceAmount(),
                    t.getTotalPriceAmount(),
                    0,
                    t.getStatus(),
                    t.getDate(),
                    t.getPaymentMethod(),
                    detailResponses
            );
        }).collect(Collectors.toList());
    }

    @Override
    public List<TransactionDetailResponse> getTransactionDetails(String email) {
        log.info("Mengambil detail transaksi untuk email: '{}'", email);
        return transactionDetailRepository.findByUserEmail(email).stream()
                .map(d -> new TransactionDetailResponse(
                        d.getId(),
                        d.getTransactionId(),
                        d.getProductId(),
                        d.getProductName(),
                        d.getQuantity(),
                        d.getTotalPriceProduct()
                ))
                .collect(Collectors.toList());
    }
}
