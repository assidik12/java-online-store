package controller;

import model.dto.request.ProductRequest;
import model.dto.response.ApiResponse;
import model.dto.response.ProductResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.ProductService;
import service.impl.ProductServiceImpl;

import java.util.List;

public class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);
    private final ProductService productService;

    public ProductController() {
        this.productService = new ProductServiceImpl();
    }

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    public ApiResponse<List<ProductResponse>> findProducts() {
        log.info("Request findProducts diterima di ProductController.");
        try {
            List<ProductResponse> products = productService.getAllProducts();
            return ApiResponse.ok("Berhasil mengambil data produk", products);
        } catch (Exception e) {
            log.error("Error pada findProducts di ProductController: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    public ApiResponse<ProductResponse> findProductById(int id) {
        log.info("Request findProductById (ID: {}) diterima di ProductController.", id);
        try {
            ProductResponse product = productService.getProductById(id);
            return ApiResponse.ok("Produk ditemukan", product);
        } catch (Exception e) {
            log.error("Error pada findProductById di ProductController: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    public ApiResponse<ProductResponse> createProduct(ProductRequest request) {
        log.info("Request createProduct diterima di ProductController.");
        try {
            ProductResponse product = productService.createProduct(request);
            return ApiResponse.ok("Produk berhasil dibuat", product);
        } catch (Exception e) {
            log.error("Error pada createProduct di ProductController: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    public ApiResponse<Boolean> updateStock(int id, int stock) {
        log.info("Request updateStock (ID: {}, Stok: {}) diterima di ProductController.", id, stock);
        try {
            boolean success = productService.updateProductStock(id, stock);
            return ApiResponse.ok("Stok berhasil diperbarui", success);
        } catch (Exception e) {
            log.error("Error pada updateStock di ProductController: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }
}