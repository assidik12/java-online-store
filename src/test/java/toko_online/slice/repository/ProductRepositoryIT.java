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
import toko_online.repository.ProductRepository;
import toko_online.repository.impl.ProductRepositoryImpl;
import toko_online.support.TestDataFactory;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(ProductRepositoryImpl.class)
class ProductRepositoryIT {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanUp() {
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
        jdbcTemplate.execute("TRUNCATE TABLE products");
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
    }

    @Test
    @DisplayName("save, findById, update, and updateStock round-trip")
    void productCrud_success() {
        Product p = TestDataFactory.createProduct("Webcam HD", 450000.0, 10);
        Product saved = productRepository.save(p);

        assertThat(saved.getId()).isNotNull();

        Optional<Product> found = productRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Webcam HD");

        boolean stockUpdated = productRepository.updateStock(saved.getId(), 8);
        assertThat(stockUpdated).isTrue();

        Optional<Product> locked = productRepository.findByIdForUpdate(saved.getId());
        assertThat(locked).isPresent();
        assertThat(locked.get().getStock()).isEqualTo(8);
    }
}
