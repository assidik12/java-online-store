package toko_online.slice.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import toko_online.model.entity.Product;
import toko_online.model.entity.Transaction;
import toko_online.model.entity.TransactionDetail;
import toko_online.model.entity.User;
import toko_online.model.enums.PaymentMethod;
import toko_online.model.enums.Role;
import toko_online.model.enums.TransactionStatus;
import toko_online.repository.ProductRepository;
import toko_online.repository.TransactionDetailRepository;
import toko_online.repository.TransactionRepository;
import toko_online.repository.UserRepository;
import toko_online.repository.impl.ProductRepositoryImpl;
import toko_online.repository.impl.TransactionDetailRepositoryImpl;
import toko_online.repository.impl.TransactionRepositoryImpl;
import toko_online.repository.impl.UserRepositoryImpl;
import toko_online.support.TestDataFactory;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TransactionDetailRepositoryImpl.class, TransactionRepositoryImpl.class, ProductRepositoryImpl.class, UserRepositoryImpl.class})
class TransactionDetailRepositoryIT {

    @Autowired
    private TransactionDetailRepository transactionDetailRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanUp() {
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
        jdbcTemplate.execute("TRUNCATE TABLE transaction_details");
        jdbcTemplate.execute("TRUNCATE TABLE transactions");
        jdbcTemplate.execute("TRUNCATE TABLE products");
        jdbcTemplate.execute("TRUNCATE TABLE users");
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
    }

    @Test
    @DisplayName("save, findByTransactionId, and findByUserEmail")
    void transactionDetailCrud_success() {
        User user = TestDataFactory.createUser("buyer1", "buyer1@mail.com", Role.USER);
        userRepository.save(user);

        Product p = TestDataFactory.createProduct("Speaker", 200000.0, 5);
        Product savedP = productRepository.save(p);

        Transaction tx = new Transaction("tx-det-1", "buyer1@mail.com", 400000, TransactionStatus.PAID, new Date(), PaymentMethod.CASH);
        transactionRepository.save(tx);

        TransactionDetail detail = new TransactionDetail("tx-det-1", savedP.getId(), 2, 400000);
        transactionDetailRepository.save(detail);

        List<TransactionDetail> byTx = transactionDetailRepository.findByTransactionId("tx-det-1");
        assertThat(byTx).hasSize(1);
        assertThat(byTx.get(0).getTotalPriceProduct()).isEqualTo(400000);

        List<TransactionDetail> byEmail = transactionDetailRepository.findByUserEmail("buyer1@mail.com");
        assertThat(byEmail).hasSize(1);
    }
}
