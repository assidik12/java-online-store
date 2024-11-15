package toko.online.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import toko.online.helper.connentionDb;
import toko.online.model.Product;

public class ProductController  {
   Connection connection = new connentionDb().getConnection();


    public List<Product> findProducts() {
        List<Product> productList = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM product");
            ResultSet products = statement.executeQuery();
        while (products.next()) {
            Product product = new Product(
                products.getInt("id"),
                products.getString("name"),
                products.getDouble("price"),
                products.getInt("stock")
            );
            
            productList.add(product);
        }
        } catch (SQLException e) {
            // Tampilkan pesan error
            throw new RuntimeException(e.getErrorCode() + ": " + e.getMessage());
        }
        return productList;
    }

    public Product findProductById(int id) {
        Product product = null;
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM product WHERE id = ?");
            statement.setInt(1, id);
            ResultSet products = statement.executeQuery();
            if (products.next()) {
                product = new Product(
                    products.getInt("id"),
                    products.getString("name"),
                    products.getDouble("price"),
                    products.getInt("stock")
                );
            }
        } catch (SQLException e) {
            // Tampilkan pesan error
            throw new RuntimeException(e.getErrorCode() + ": " + e.getMessage());
        }
        return product;
    }
}