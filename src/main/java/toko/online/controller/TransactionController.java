package toko.online.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import toko.online.helper.connentionDb;

import toko.online.model.transaction;

public class TransactionController {
    Connection connection = new connentionDb().getConnection();

    public boolean buyProduct(transaction transaction) {

        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO transactions (id_transaction, user_email, total_price_amount, status, date) VALUES (?, ?, ?, ?, ?)");
            statement.setString(1, transaction.getTransactionId());
            statement.setString(2, transaction.getEmail());
            statement.setInt(3, transaction.getTotal_price_amount());
            statement.setBoolean(4, transaction.isStatus());
            statement.setDate(5, new java.sql.Date(transaction.getDate().getTime()));
            
            PreparedStatement datailStatement = connection.prepareStatement("insert into transactions_details (transaction_id, product_id, total_price_product, quantity) values (?,?,?,?)");
            datailStatement.setString(1, transaction.getTransactionId());
            datailStatement.setInt(2, transaction.getId_product());
            datailStatement.setInt(3, transaction.getTotal_price_product());
            datailStatement.setInt(4, transaction.getQty());

            statement.executeUpdate();
            datailStatement.executeUpdate();


            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e.getErrorCode() + ": " + e.getMessage());
        }
    }

    public ResultSet viewTransactions(String email){

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM transactions inner join transactions_details on transactions.id_transaction = transactions_details.transaction_id inner join product on transactions_details.product_id = product.id WHERE user_email = ?");
            statement.setString(1, email);            
            ResultSet products = statement.executeQuery();
            // while (products.next()) {
            //     System.out.println("");
            //     System.out.println("=".repeat(80) );
            //     System.out.println("Transaction ID: " + products.getString("id_transaction"));
            //     System.out.println("product name: " + products.getString("name"));
            //     System.out.println("jumlah product yang di beli: " + products.getInt("quantity"));
            //     System.out.println("jumlah harga product: " + products.getInt("total_price_product"));
            //     System.out.println("jumlah yang dibayar: " + products.getInt("total_price_amount"));
            //     System.out.println("Status: " + (products.getBoolean("status") ? "Paid" : "Unpaid"));
            //     System.out.println("user email: " + products.getString("user_email"));
            //     System.out.println("Tanggal: " + products.getDate("date"));
            //     System.out.println("=".repeat(80) );
            // }
            return products;
        } catch (SQLException e) {
            throw new RuntimeException(e.getErrorCode() + ": " + e.getMessage());
        }
    }


    public boolean updateProduct(int id, int stock) {
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE product SET stock = ? WHERE id = ?");
            statement.setInt(1, stock);
            statement.setInt(2, id);
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e.getErrorCode() + ": " + e.getMessage());
        }
    }
}
