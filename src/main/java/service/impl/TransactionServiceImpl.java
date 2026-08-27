package service.impl;

import config.DatabaseConfig;
import exception.DatabaseException;
import exception.InsufficientStockException;
import exception.ResourceNotFoundException;
import exception.ValidationException;
import model.dto.request.TransactionRequest;
import model.dto.response.TransactionDetailResponse;
import model.dto.response.TransactionResponse;
import model.entity.Product;
import model.entity.Transaction;
import model.entity.TransactionDetail;
import model.enums.PaymentMethod;
import model.enums.TransactionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.ProductRepository;
import repository.TransactionDetailRepository;
import repository.TransactionRepository;
import repository.impl.ProductRepositoryImpl;
import repository.impl.TransactionDetailRepositoryImpl;
import repository.impl.TransactionRepositoryImpl;
import service.TransactionService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class TransactionServiceImpl implements TransactionService {

    private static final Logger log = LoggerFactory.getLogger(TransactionServiceImpl.class);
    private final ProductRepository productRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionDetailRepository transactionDetailRepository;

    public TransactionServiceImpl() {
        this.productRepository = new ProductRepositoryImpl();
        this.transactionRepository = new TransactionRepositoryImpl();
        this.transactionDetailRepository = new TransactionDetailRepositoryImpl();
    }

    public TransactionServiceImpl(ProductRepository productRepository,
                                  TransactionRepository transactionRepository,
                                  TransactionDetailRepository transactionDetailRepository) {
        this.productRepository = productRepository;
        this.transactionRepository = transactionRepository;
        this.transactionDetailRepository = transactionDetailRepository;
    }

    @Override
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

        // Atomic transaction using Connection & Transactional Management
        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // 1. Ambil & Kunci Produk
                Product product = productRepository.findById(conn, request.getProductId())
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
                productRepository.updateStock(conn, product.getId(), newStock);

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
                transactionRepository.save(conn, transaction);

                // 6. Simpan Detail Transaksi
                TransactionDetail detail = new TransactionDetail(
                        transactionId,
                        product.getId(),
                        request.getQuantity(),
                        totalPriceProduct
                );
                transactionDetailRepository.save(conn, detail);

                // Commit Transaction
                conn.commit();
                log.info("Transaksi berhasil diproses & di-commit! ID Transaksi: {}, Total: Rp. {}, Kembalian: Rp. {}",
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
            } catch (Exception e) {
                log.error("Terjadi kesalahan saat memproses transaksi, melakukan rollback: {}", e.getMessage());
                conn.rollback();
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }
                throw new DatabaseException("Gagal memproses transaksi: " + e.getMessage(), e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            log.error("Kesalahan database saat membuat transaksi: {}", e.getMessage(), e);
            throw new DatabaseException("Kesalahan koneksi database: " + e.getMessage(), e);
        }
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
