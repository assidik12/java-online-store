package toko_online.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import toko_online.model.dto.request.ProductRequest;
import toko_online.model.dto.request.UpdateProductRequest;
import toko_online.model.dto.response.ApiResponse;
import toko_online.model.dto.response.ProductResponse;
import toko_online.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProducts() {
        log.info("REST Request: GET /api/v1/products");
        List<ProductResponse> products = productService.getAllProducts();
        return ResponseEntity.ok(ApiResponse.ok("Berhasil mengambil data produk", products));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable Integer id) {
        log.info("REST Request: GET /api/v1/products/{}", id);
        ProductResponse product = productService.getProductById(id);
        return ResponseEntity.ok(ApiResponse.ok("Produk ditemukan", product));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@Valid @RequestBody ProductRequest request) {
        log.info("REST Request: POST /api/v1/products dengan nama: {}", request.getName());
        ProductResponse product = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Produk berhasil dibuat", product));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(@PathVariable Integer id,
            @Valid @RequestBody UpdateProductRequest request) {
        log.info("REST Request: PUT /api/v1/products/{}", id);
        ProductResponse product = productService.updateProduct(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Produk berhasil diupdate", product));
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<ApiResponse<Boolean>> updateStock(@PathVariable Integer id, @RequestParam Integer newStock) {
        log.info("REST Request: PATCH /api/v1/products/{}/stock?newStock={}", id, newStock);
        boolean success = productService.updateProductStock(id, newStock);
        return ResponseEntity.ok(ApiResponse.ok("Stok berhasil diperbarui", success));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Boolean>> deleteProduct(@PathVariable Integer id) {
        log.info("REST Request: DELETE /api/v1/products/{}", id);
        boolean success = productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.ok("Produk berhasil dihapus", success));
    }
}