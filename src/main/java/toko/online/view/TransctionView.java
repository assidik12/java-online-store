package toko.online.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;

import toko.online.controller.TransactionController;
import toko.online.model.Product;
import toko.online.model.transaction;

public class TransctionView extends JFrame {
    private JTable transactionTable;
    private DefaultTableModel tableModel;
    private TransactionController transactionController;
    private JTextField emailField;

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

    
    public TransctionView(){
        transactionController = new TransactionController();

        // Pengaturan JFrame
        setTitle("Histori Transaksi");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        
        // Kolom tabel
        String[] columnNames = {"ID Transaksi", "Nama Produk", "Kuantitas", "Total Harga Produk", "Total Dibayar", "Status", "Email Pengguna", "Tanggal", "Metode Pembayaran"};
        

        // Model tabel
        tableModel = new DefaultTableModel(columnNames, 0);
        transactionTable = new JTable(tableModel);

        // Menambahkan tabel ke JScrollPane
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        add(scrollPane, BorderLayout.CENTER);

        // Panel input email
        JPanel inputPanel = new JPanel(new FlowLayout());
        JLabel emailLabel = new JLabel("Email Pengguna:");
        emailField = new JTextField(20);

              inputPanel.add(emailLabel);
              inputPanel.add(emailField);

  
        add(inputPanel, BorderLayout.NORTH);

        // Tombol untuk menampilkan semua transaksi
        JButton showAllButton = new JButton("Tampilkan Semua Transaksi");
        showAllButton.addActionListener(e -> {
            try {
                showTransactions();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        });
        add(showAllButton, BorderLayout.SOUTH);

        // Muat data awal
        try {
            showTransactions();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    private void showTransactions() throws SQLException {
        // Hapus semua baris di tabel
        tableModel.setRowCount(0);

        // Ambil data transaksi
        ResultSet transactions = transactionController.viewTransactions(emailField.getText());

        // Tampilkan data ke tabel
        while (transactions.next()) {
            Object[] row = {
                transactions.getString("id_transaction"),
                transactions.getString("name"),
                transactions.getInt("quantity"),
                String.format("%,.0f", (double) transactions.getInt("total_price_product")).replace(',', '.'),
                String.format("%,.0f", (double) transactions.getInt("total_price_amount")).replace(',', '.'),
                transactions.getBoolean("status") ? "Paid" : "Unpaid",
                transactions.getString("user_email"),
                transactions.getDate("date"),
                transactions.getString("payment_method")
            };
            tableModel.addRow(row);
        }
    }


}