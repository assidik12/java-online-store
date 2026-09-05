package toko_online.unit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toko_online.exception.ResourceNotFoundException;
import toko_online.exception.ValidationException;
import toko_online.model.dto.request.ProductRequest;
import toko_online.model.dto.request.UpdateProductRequest;
import toko_online.model.dto.response.ProductResponse;
import toko_online.model.entity.Product;
import toko_online.repository.ProductRepository;
import toko_online.service.impl.ProductServiceImpl;
import toko_online.support.TestDataFactory;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    private ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        productService = new ProductServiceImpl(productRepository);
    }

    @Test
    @DisplayName("getAllProducts: maps entity list to response list")
    void getAllProducts_success() {
        Product p1 = TestDataFactory.createProduct("Laptop", 15000000.0, 5);
        Product p2 = TestDataFactory.createProduct("Mouse", 200000.0, 10);
        when(productRepository.findAll()).thenReturn(List.of(p1, p2));

        List<ProductResponse> result = productService.getAllProducts();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Laptop");
        assertThat(result.get(1).getName()).isEqualTo("Mouse");
    }

    @Test
    @DisplayName("getProductById: invalid id throws ValidationException")
    void getProductById_invalidId_throwsValidationException() {
        assertThatThrownBy(() -> productService.getProductById(null))
                .isInstanceOf(ValidationException.class);
        assertThatThrownBy(() -> productService.getProductById(0))
                .isInstanceOf(ValidationException.class);
        assertThatThrownBy(() -> productService.getProductById(-1))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("getProductById: not found throws ResourceNotFoundException")
    void getProductById_notFound_throwsResourceNotFoundException() {
        when(productRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductById(999))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("tidak ditemukan.");
    }

    @Test
    @DisplayName("getProductById: found returns mapped ProductResponse")
    void getProductById_found_returnsProductResponse() {
        Product product = TestDataFactory.createProduct("Monitor", 2500000.0, 7);
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        ProductResponse response = productService.getProductById(product.getId());

        assertThat(response.getId()).isEqualTo(product.getId());
        assertThat(response.getName()).isEqualTo("Monitor");
    }

    @Test
    @DisplayName("createProduct: invalid inputs throw ValidationException")
    void createProduct_invalidInput_throwsValidationException() {
        assertThatThrownBy(() -> productService.createProduct(null))
                .isInstanceOf(ValidationException.class);

        ProductRequest emptyName = TestDataFactory.validProductRequest("  ", 1000.0, 10);
        assertThatThrownBy(() -> productService.createProduct(emptyName))
                .isInstanceOf(ValidationException.class);

        ProductRequest negativePrice = TestDataFactory.validProductRequest("Good", -1.0, 10);
        assertThatThrownBy(() -> productService.createProduct(negativePrice))
                .isInstanceOf(ValidationException.class);

        ProductRequest negativeStock = TestDataFactory.validProductRequest("Good", 1000.0, -5);
        assertThatThrownBy(() -> productService.createProduct(negativeStock))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("createProduct: valid request saves and returns ProductResponse")
    void createProduct_valid_success() {
        ProductRequest req = TestDataFactory.validProductRequest("Headset", 500000.0, 15);
        Product saved = new Product(10, req.getName(), req.getPrice(), req.getStock());
        when(productRepository.save(any(Product.class))).thenReturn(saved);

        ProductResponse response = productService.createProduct(req);

        assertThat(response.getId()).isEqualTo(10);
        assertThat(response.getName()).isEqualTo("Headset");
        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("updateProduct: invalid inputs throw ValidationException")
    void updateProduct_invalidInput_throwsValidationException() {
        UpdateProductRequest valid = TestDataFactory.validUpdateProductRequest("Keyboard", 300000.0, 10);

        assertThatThrownBy(() -> productService.updateProduct(null, valid))
                .isInstanceOf(ValidationException.class);
        assertThatThrownBy(() -> productService.updateProduct(0, valid))
                .isInstanceOf(ValidationException.class);
        assertThatThrownBy(() -> productService.updateProduct(1, null))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("updateProduct: valid update returns updated response")
    void updateProduct_valid_success() {
        UpdateProductRequest req = TestDataFactory.validUpdateProductRequest("Updated Name", 450000.0, 20);
        Product updated = new Product(5, req.getName(), req.getPrice(), req.getStock());
        when(productRepository.update(any(Product.class))).thenReturn(updated);

        ProductResponse response = productService.updateProduct(5, req);

        assertThat(response.getId()).isEqualTo(5);
        assertThat(response.getName()).isEqualTo("Updated Name");
    }

    @Test
    @DisplayName("updateProductStock: not found throws ResourceNotFoundException")
    void updateProductStock_notFound_throwsResourceNotFoundException() {
        when(productRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.updateProductStock(99, 50))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("updateProductStock: valid stock updates successfully")
    void updateProductStock_valid_success() {
        Product product = TestDataFactory.inStockProduct(10);
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(productRepository.updateStock(product.getId(), 25)).thenReturn(true);

        boolean updated = productService.updateProductStock(product.getId(), 25);

        assertThat(updated).isTrue();
    }

    @Test
    @DisplayName("deleteProduct: not found throws ResourceNotFoundException")
    void deleteProduct_notFound_throwsResourceNotFoundException() {
        when(productRepository.findById(88)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.deleteProduct(88))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("deleteProduct: found deletes successfully")
    void deleteProduct_found_success() {
        Product product = TestDataFactory.inStockProduct(5);
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(productRepository.deleteById(product.getId())).thenReturn(true);

        boolean deleted = productService.deleteProduct(product.getId());

        assertThat(deleted).isTrue();
    }
}
