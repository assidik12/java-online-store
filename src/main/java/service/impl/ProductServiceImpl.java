package service.impl;

import exception.ResourceNotFoundException;
import exception.ValidationException;
import model.dto.request.ProductRequest;
import model.dto.response.ProductResponse;
import model.entity.Product;
import repository.ProductRepository;
import repository.impl.ProductRepositoryImpl;
import service.ProductService;

import java.util.List;
import java.util.stream.Collectors;

public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl() {
        this.productRepository = new ProductRepositoryImpl();
    }

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponse getProductById(Integer id) {
        if (id == null || id <= 0) {
            throw new ValidationException("ID Produk tidak valid.");
        }
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produk dengan ID " + id + " tidak ditemukan."));
        return mapToResponse(product);
    }

    @Override
    public ProductResponse createProduct(ProductRequest request) {
        if (request == null) {
            throw new ValidationException("Data produk tidak boleh kosong.");
        }
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new ValidationException("Nama produk wajib diisi.");
        }
        if (request.getPrice() == null || request.getPrice() < 0) {
            throw new ValidationException("Harga produk tidak boleh negatif.");
        }
        if (request.getStock() == null || request.getStock() < 0) {
            throw new ValidationException("Stok produk tidak boleh negatif.");
        }

        Product product = new Product(request.getName().trim(), request.getPrice(), request.getStock());
        Product saved = productRepository.save(product);
        return mapToResponse(saved);
    }

    @Override
    public boolean updateProductStock(Integer id, Integer newStock) {
        if (id == null || id <= 0) {
            throw new ValidationException("ID Produk tidak valid.");
        }
        if (newStock == null || newStock < 0) {
            throw new ValidationException("Stok tidak boleh negatif.");
        }
        if (productRepository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("Produk dengan ID " + id + " tidak ditemukan.");
        }
        return productRepository.updateStock(id, newStock);
    }

    private ProductResponse mapToResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStock()
        );
    }
}
