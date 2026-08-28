package toko_online.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import toko_online.exception.ResourceNotFoundException;
import toko_online.exception.ValidationException;
import toko_online.model.dto.request.ProductRequest;
import toko_online.model.dto.request.UpdateProductRequest;
import toko_online.model.dto.response.ProductResponse;
import toko_online.model.entity.Product;
import toko_online.repository.ProductRepository;
import toko_online.service.ProductService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);
    private final ProductRepository productRepository;

    // Constructor Injection: Spring otomatis menyuntikkan (inject) bean
    // ProductRepository
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        log.info("Mengambil semua data produk dari database...");
        List<Product> products = productRepository.findAll();
        log.info("Berhasil mengambil {} produk.", products.size());
        return products.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponse getProductById(Integer id) {
        log.info("Mencari produk dengan ID: {}", id);
        if (id == null || id <= 0) {
            log.warn("ID Produk tidak valid: {}", id);
            throw new ValidationException("ID Produk tidak valid.");
        }
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Produk dengan ID {} tidak ditemukan.", id);
                    return new ResourceNotFoundException("Produk dengan ID " + id + " tidak ditemukan.");
                });
        return mapToResponse(product);
    }

    @Override
    public ProductResponse createProduct(ProductRequest request) {
        log.info("Membuat produk baru: {}", request != null ? request.getName() : "null");
        if (request == null) {
            log.warn("Gagal membuat produk: Request kosong.");
            throw new ValidationException("Data produk tidak boleh kosong.");
        }
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            log.warn("Gagal membuat produk: Nama produk kosong.");
            throw new ValidationException("Nama produk wajib diisi.");
        }
        if (request.getPrice() == null || request.getPrice() < 0) {
            log.warn("Gagal membuat produk: Harga negatif.");
            throw new ValidationException("Harga produk tidak boleh negatif.");
        }
        if (request.getStock() == null || request.getStock() < 0) {
            log.warn("Gagal membuat produk: Stok negatif.");
            throw new ValidationException("Stok produk tidak boleh negatif.");
        }

        Product product = new Product(request.getName().trim(), request.getPrice(), request.getStock());
        Product saved = productRepository.save(product);
        log.info("Produk baru berhasil disimpan: {} (ID: {})", saved.getName(), saved.getId());
        return mapToResponse(saved);
    }

    @Override
    public ProductResponse updateProduct(Integer id, UpdateProductRequest request) {
        log.info("Mengupdate produk dengan ID: {} menjadi: {}", id, request != null ? request.getName() : "null");
        if (request == null) {
            log.warn("Gagal mengupdate produk: Request kosong.");
            throw new ValidationException("Data produk tidak boleh kosong.");
        }
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            log.warn("Gagal mengupdate produk: Nama produk kosong.");
            throw new ValidationException("Nama produk wajib diisi.");
        }
        if (request.getPrice() == null || request.getPrice() < 0) {
            log.warn("Gagal mengupdate produk: Harga negatif.");
            throw new ValidationException("Harga produk tidak boleh negatif.");
        }
        if (request.getStock() == null || request.getStock() < 0) {
            log.warn("Gagal mengupdate produk: Stok negatif.");
            throw new ValidationException("Stok produk tidak boleh negatif.");
        }
        if (id == null || id <= 0) {
            log.warn("Gagal mengupdate produk: ID Produk tidak valid: {}", id);
            throw new ValidationException("IDProduk tidak valid.");
        }
        Product product = new Product(id, request.getName().trim(), request.getPrice(), request.getStock());
        Product updated = productRepository.update(product);
        log.info("Produk berhasil diupdate: {} (ID: {})", updated.getName(), updated.getId());
        return mapToResponse(updated);
    }

    @Override
    public boolean updateProductStock(Integer id, Integer newStock) {
        log.info("Mengupdate stok produk ID: {} menjadi: {}", id, newStock);
        if (id == null || id <= 0) {
            log.warn("Gagal update stok: ID Produk tidak valid: {}", id);
            throw new ValidationException("ID Produk tidak valid.");
        }
        if (newStock == null || newStock < 0) {
            log.warn("Gagal update stok: Nilai stok negatif: {}", newStock);
            throw new ValidationException("Stok tidak boleh negatif.");
        }
        if (productRepository.findById(id).isEmpty()) {
            log.warn("Gagal update stok: Produk ID {} tidak ditemukan.", id);
            throw new ResourceNotFoundException("Produk dengan ID " + id + " tidak ditemukan.");
        }
        boolean updated = productRepository.updateStock(id, newStock);
        log.info("Pembaruan stok produk ID {} berhasil: {}", id, updated);
        return updated;
    }

    @Override
    public boolean deleteProduct(Integer id) {
        log.info("Menghapus produk dengan ID: {}", id);
        if (id == null || id <= 0) {
            log.warn("Gagal menghapus produk: IDProduk tidak valid: {}", id);
            throw new ValidationException("IDProduk tidak valid.");
        }
        if (productRepository.findById(id).isEmpty()) {
            log.warn("Gagal menghapus produk: Produk ID {} tidak ditemukan.", id);
            throw new ResourceNotFoundException("Produk dengan ID " + id + " tidak ditemukan.");
        }
        boolean deleted = productRepository.deleteById(id);
        log.info("Penghapusan produk ID {} berhasil: {}", id, deleted);
        return deleted;
    }

    private ProductResponse mapToResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStock());
    }
}
