package toko_online.unit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toko_online.exception.InsufficientStockException;
import toko_online.exception.ResourceNotFoundException;
import toko_online.exception.ValidationException;
import toko_online.model.dto.request.TransactionRequest;
import toko_online.model.dto.response.TransactionResponse;
import toko_online.model.entity.Product;
import toko_online.model.entity.Transaction;
import toko_online.model.entity.TransactionDetail;
import toko_online.model.enums.PaymentMethod;
import toko_online.model.enums.TransactionStatus;
import toko_online.repository.ProductRepository;
import toko_online.repository.TransactionDetailRepository;
import toko_online.repository.TransactionRepository;
import toko_online.service.impl.TransactionServiceImpl;
import toko_online.support.TestDataFactory;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionDetailRepository transactionDetailRepository;

    private TransactionServiceImpl transactionService;

    @BeforeEach
    void setUp() {
        transactionService = new TransactionServiceImpl(productRepository, transactionRepository, transactionDetailRepository);
    }

    @Test
    @DisplayName("createTransaction: null request throws ValidationException")
    void createTransaction_null_throwsValidationException() {
        assertThatThrownBy(() -> transactionService.createTransaction(null))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("createTransaction: invalid productId or quantity throws ValidationException")
    void createTransaction_invalidIdOrQty_throwsValidationException() {
        TransactionRequest req1 = TestDataFactory.validTransactionRequest(0, 1, "test@mail.com", 10000);
        assertThatThrownBy(() -> transactionService.createTransaction(req1))
                .isInstanceOf(ValidationException.class);

        TransactionRequest req2 = TestDataFactory.validTransactionRequest(1, 0, "test@mail.com", 10000);
        assertThatThrownBy(() -> transactionService.createTransaction(req2))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("createTransaction: product not found throws ResourceNotFoundException")
    void createTransaction_productNotFound_throwsResourceNotFoundException() {
        TransactionRequest req = TestDataFactory.validTransactionRequest(999, 2, "test@mail.com", 100000);
        when(productRepository.findByIdForUpdate(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.createTransaction(req))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("createTransaction: stock less than quantity throws InsufficientStockException")
    void createTransaction_insufficientStock_throwsInsufficientStockException() {
        Product product = new Product(10, "USB Flashdisk", 50000.0, 2);
        TransactionRequest req = TestDataFactory.validTransactionRequest(10, 5, "test@mail.com", 300000);
        when(productRepository.findByIdForUpdate(10)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> transactionService.createTransaction(req))
                .isInstanceOf(InsufficientStockException.class)
                .hasMessageContaining("Stok tidak mencukupi");
    }

    @Test
    @DisplayName("createTransaction: paid amount less than total price throws ValidationException")
    void createTransaction_underpaid_throwsValidationException() {
        Product product = new Product(10, "USB Flashdisk", 50000.0, 5);
        TransactionRequest req = TestDataFactory.validTransactionRequest(10, 2, "test@mail.com", 80000); // needs 100_000
        when(productRepository.findByIdForUpdate(10)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> transactionService.createTransaction(req))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("kurang dari total harga");
    }

    @Test
    @DisplayName("createTransaction: valid purchase locks product, updates stock, and saves transaction details")
    void createTransaction_valid_success() {
        Product product = new Product(10, "USB Flashdisk", 50000.0, 10);
        TransactionRequest req = TestDataFactory.validTransactionRequest(10, 2, "buyer@mail.com", 120000);
        when(productRepository.findByIdForUpdate(10)).thenReturn(Optional.of(product));

        TransactionResponse response = transactionService.createTransaction(req);

        assertThat(response).isNotNull();
        assertThat(response.getTotalPriceAmount()).isEqualTo(100000);
        assertThat(response.getPaidAmount()).isEqualTo(120000);
        assertThat(response.getChangeAmount()).isEqualTo(20000);
        assertThat(response.getStatus()).isEqualTo(TransactionStatus.PAID);

        verify(productRepository).updateStock(10, 8);
        verify(transactionRepository).save(any(Transaction.class));
        verify(transactionDetailRepository).save(any(TransactionDetail.class));
    }

    @Test
    @DisplayName("getTransactionsByUserEmail: null email returns all transactions")
    void getTransactionsByUserEmail_nullEmail_callsFindAll() {
        Transaction t = new Transaction("tx-1", "user@test.com", 50000, TransactionStatus.PAID, new Date(), PaymentMethod.CASH);
        when(transactionRepository.findAll()).thenReturn(List.of(t));
        when(transactionDetailRepository.findByTransactionId("tx-1")).thenReturn(List.of());

        List<TransactionResponse> result = transactionService.getTransactionsByUserEmail(null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTransactionId()).isEqualTo("tx-1");
    }
}
