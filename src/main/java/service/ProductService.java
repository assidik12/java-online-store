package service;

import model.dto.request.ProductRequest;
import model.dto.response.ProductResponse;

import java.util.List;

public interface ProductService {
    List<ProductResponse> getAllProducts();
    ProductResponse getProductById(Integer id);
    ProductResponse createProduct(ProductRequest request);
    boolean updateProductStock(Integer id, Integer newStock);
}
