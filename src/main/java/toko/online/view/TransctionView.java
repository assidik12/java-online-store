package toko.online.view;

import toko.online.controller.TransactionController;
import toko.online.model.Product;
import toko.online.model.transaction;

public class TransctionView {
    TransactionController transactionController = new TransactionController();
    ProductView productView = new ProductView();

    public void buyProduct(transaction transaction) {
        // logic bussiness

       Product product = ProductView.getProductsById(transaction.getId_product());
       if (product == null) {
        throw new RuntimeException("Product not found");
       }

       if (transaction.getQty() > product.getStock()) {
        throw new RuntimeException("Stock not enough");
       }

       if (transactionController.updateProduct(product.getId(), transaction.getQty())) {
        transaction.setTotal_price_product((int) (product.getPrice() * transaction.getQty())); 
        transactionController.buyProduct(transaction);
       }
    }

    public void viewTransactions(String email) {
        transactionController.viewTransactions(email);
    }
}
