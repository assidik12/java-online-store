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
import toko_online.model.entity.Transaction;
import toko_online.model.entity.User;
import toko_online.model.enums.PaymentMethod;
import toko_online.model.enums.Role;
import toko_online.model.enums.TransactionStatus;
import toko_online.repository.TransactionRepository;
import toko_online.repository.UserRepository;
import toko_online.repository.impl.TransactionRepositoryImpl;
import toko_online.repository.impl.UserRepositoryImpl;
import toko_online.support.TestDataFactory;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TransactionRepositoryImpl.class, UserRepositoryImpl.class})
class TransactionRepositoryIT {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanUp() {
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
        jdbcTemplate.execute("TRUNCATE TABLE transactions");
        jdbcTemplate.execute("TRUNCATE TABLE users");
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
    }

    @Test
    @DisplayName("save, findById, and findByUserEmail")
    void transactionCrud_success() {
        User user = TestDataFactory.createUser("txuser", "txuser@mail.com", Role.USER);
        userRepository.save(user);

        Transaction tx = new Transaction("tx-abc", "txuser@mail.com", 150000, TransactionStatus.PAID, new Date(), PaymentMethod.CASH);
        transactionRepository.save(tx);

        Optional<Transaction> found = transactionRepository.findById("tx-abc");
        assertThat(found).isPresent();
        assertThat(found.get().getTotalPriceAmount()).isEqualTo(150000);

        List<Transaction> byEmail = transactionRepository.findByUserEmail("txuser@mail.com");
        assertThat(byEmail).hasSize(1);
    }
}
