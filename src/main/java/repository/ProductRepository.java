package repository;

import model.entity.Product;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    Product save(Product product);
    Product save(Connection conn, Product product);
    Optional<Product> findById(Integer id);
    Optional<Product> findById(Connection conn, Integer id);
    List<Product> findAll();
    boolean updateStock(Integer id, Integer newStock);
    boolean updateStock(Connection conn, Integer id, Integer newStock);
    boolean deleteById(Integer id);
}
