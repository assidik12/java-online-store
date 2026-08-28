package toko_online.service;

import java.util.List;

import toko_online.model.dto.request.ProductRequest;
import toko_online.model.dto.request.UpdateProductRequest;
import toko_online.model.dto.response.ProductResponse;

public interface ProductService {
    List<ProductResponse> getAllProducts();

    ProductResponse getProductById(Integer id);

    ProductResponse createProduct(ProductRequest request);

    ProductResponse updateProduct(Integer id, UpdateProductRequest request);

    boolean updateProductStock(Integer id, Integer newStock);

    boolean deleteProduct(Integer id);
}
