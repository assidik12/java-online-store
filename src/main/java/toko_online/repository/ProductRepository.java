package toko_online.repository;

import toko_online.model.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    Product save(Product product);

    Product update(Product product);

    Optional<Product> findById(Integer id);

    List<Product> findAll();

    boolean updateStock(Integer id, Integer newStock);

    boolean deleteById(Integer id);
}
