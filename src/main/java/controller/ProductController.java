package controller;

import model.dto.request.ProductRequest;
import model.dto.response.ApiResponse;
import model.dto.response.ProductResponse;
import service.ProductService;
import service.impl.ProductServiceImpl;

import java.util.List;

public class ProductController {

    private final ProductService productService;

    public ProductController() {
        this.productService = new ProductServiceImpl();
    }

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    public ApiResponse<List<ProductResponse>> findProducts() {
        try {
            List<ProductResponse> products = productService.getAllProducts();
            return ApiResponse.ok("Berhasil mengambil data produk", products);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    public ApiResponse<ProductResponse> findProductById(int id) {
        try {
            ProductResponse product = productService.getProductById(id);
            return ApiResponse.ok("Produk ditemukan", product);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    public ApiResponse<ProductResponse> createProduct(ProductRequest request) {
        try {
            ProductResponse product = productService.createProduct(request);
            return ApiResponse.ok("Produk berhasil dibuat", product);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    public ApiResponse<Boolean> updateStock(int id, int stock) {
        try {
            boolean success = productService.updateProductStock(id, stock);
            return ApiResponse.ok("Stok berhasil diperbarui", success);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}