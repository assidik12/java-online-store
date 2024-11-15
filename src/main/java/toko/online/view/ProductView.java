package toko.online.view;

import toko.online.model.Product;

import java.util.List;


import toko.online.controller.ProductController;

public class ProductView {

    static ProductController productController = new ProductController();
    
        public static Product getProductsById(int id) {
        Product product = productController.findProductById(id);
        if (product != null) {
            return product;
        }
        return null;
    }
    
    public void getProducts() {
         List<Product> products = productController.findProducts();
        for (Product product : products) {
            System.out.println("data barang: ");
            System.out.println("=".repeat(20) );
            System.out.print("id: " + product.getId()+ "\t");
            System.out.print("\t"+"name: " + product.getName()+"\t");
            System.out.println("price: " + product.getPrice());
            System.out.println("=".repeat(20) );
        }
    }
   }