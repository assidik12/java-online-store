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
        if (request == null) {
            throw new ValidationException("Data transaksi tidak boleh kosong.");
        }
        if (request.getProductId() == null || request.getProductId() <= 0) {
            throw new ValidationException("ID Produk harus valid dan lebih dari 0.");
        }
        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new ValidationException("Kuantitas harus lebih dari 0.");
        }
        if (request.getUserEmail() == null || request.getUserEmail().trim().isEmpty()) {
            throw new ValidationException("Email pembeli wajib diisi.");
        }
        if (request.getPaidAmount() == null || request.getPaidAmount() <= 0) {
            throw new ValidationException("Jumlah pembayaran harus lebih dari 0.");
        }

        PaymentMethod paymentMethod = request.getPaymentMethod() != null ? request.getPaymentMethod() : PaymentMethod.CASH;

        // Atomic transaction using Connection & Transactional Management
        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // 1. Ambil & Kunci Produk
                Product product = productRepository.findById(conn, request.getProductId())
                        .orElseThrow(() -> new ResourceNotFoundException("Produk dengan ID " + request.getProductId() + " tidak ditemukan."));

                // 2. Validasi Stok
                if (product.getStock() < request.getQuantity()) {
                    throw new InsufficientStockException("Stok tidak mencukupi! Stok saat ini: " + product.getStock() + ", diminta: " + request.getQuantity());
                }

                // 3. Hitung Total Harga & Kembalian
                int totalPriceProduct = (int) (product.getPrice() * request.getQuantity());
                if (request.getPaidAmount() < totalPriceProduct) {
                    throw new ValidationException("Jumlah uang dibayarkan (" + request.getPaidAmount() + ") kurang dari total harga (" + totalPriceProduct + ").");
                }
                int changeAmount = request.getPaidAmount() - totalPriceProduct;

                // 4. Update Stok Produk
                int newStock = product.getStock() - request.getQuantity();
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
                conn.rollback();
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }
                throw new DatabaseException("Gagal memproses transaksi: " + e.getMessage(), e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Kesalahan koneksi database: " + e.getMessage(), e);
        }
    }

    @Override
    public List<TransactionResponse> getTransactionsByUserEmail(String email) {
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
                    t.getTotalPriceAmount(), // default paid
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
