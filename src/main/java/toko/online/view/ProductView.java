package toko.online.view;

import toko.online.model.Product;
import toko.online.model.transaction;

import java.util.List;
import java.util.UUID;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import java.awt.*;

import toko.online.controller.ProductController;
import toko.online.controller.TransactionController;

public class ProductView extends JFrame {    
    private JTable productTable;
    private DefaultTableModel tableModel;
    private static ProductController productController;
       // Field transaksi
       private JTextField fieldProductId;
       private JTextField fieldQuantity;
       private JTextField fieldEmail;
       private JTextField buyyerField;
       private String[] paymentMethodOptions = {"BCA", "Cash", "alfamart", "Mandiri"};
       private JComboBox<String> payment_method;
        
        public static Product getProductsById(int id) {
        Product product = productController.findProductById(id);
        if (product != null) {
            return product;
        }
        return null;
    }
      
    public ProductView() {
        productController = new ProductController();

        // Pengaturan JFrame
        setTitle("Data Barang");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLayout(new BorderLayout());

        // Kolom tabel
        String[] columnNames = {"ID Barang", "Nama Barang", "Harga (Rp)"};

        // Model tabel
        tableModel = new DefaultTableModel(columnNames, 0);
        productTable = new JTable(tableModel);

        // Menambahkan tabel ke JScrollPane
        JScrollPane scrollPane = new JScrollPane(productTable);

        // Menambahkan tabel ke JFrame
        add(scrollPane, BorderLayout.CENTER);

        showProducts();

        // Panel input transaksi
        JPanel transactionPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        transactionPanel.setBorder(BorderFactory.createTitledBorder("Input Transaksi"));

        fieldProductId = new JTextField();
        fieldQuantity = new JTextField();
        fieldEmail= new JTextField();
        buyyerField = new JTextField();

        payment_method = new JComboBox<>(paymentMethodOptions);

        transactionPanel.setLayout(new BoxLayout(transactionPanel, BoxLayout.Y_AXIS));

        transactionPanel.add(new JLabel("ID Barang:"));
        transactionPanel.add(fieldProductId);

        transactionPanel.add(Box.createVerticalStrut(5)); // Jarak antara komponen

        transactionPanel.add(new JLabel("Kuantitas:"));
        transactionPanel.add(fieldQuantity);

        transactionPanel.add(Box.createVerticalStrut(5)); // Jarak antara komponen

        transactionPanel.add(new JLabel("Email:"));
        transactionPanel.add(fieldEmail);

        transactionPanel.add(Box.createVerticalStrut(5)); // Jarak antara komponen

        transactionPanel.add(new JLabel("Masukan jumlah uang:"));
        transactionPanel.add(buyyerField);

        transactionPanel.add(Box.createVerticalStrut(5)); // Jarak antara komponen

        transactionPanel.add(new JLabel("Metode Pembayaran:"));
        transactionPanel.add(payment_method);


        JButton saveTransactionButton = new JButton("Simpan Transaksi");
        saveTransactionButton.addActionListener(e -> saveTransaction());
        transactionPanel.add(saveTransactionButton);

        add(transactionPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void showProducts() {
        List<Product> products = productController.findProducts();
        for (Product product : products) {
            Object[] row = {product.getId(), product.getName(), String.format("Rp.,%,.2f", product.getPrice())};
            tableModel.addRow(row);
        }
    }

        private void saveTransaction() {
        try {
            int productId = Integer.parseInt(fieldProductId.getText());
            int quantity = Integer.parseInt(fieldQuantity.getText());
            String email = fieldEmail.getText();
            String transactionId = UUID.randomUUID().toString();

            Product product = getProductsById(productId);

            if (product == null) {
                JOptionPane.showMessageDialog(this, "ID Barang tidak ditemukan!", "Kesalahan", JOptionPane.ERROR_MESSAGE);
                return;
            };
            int total_price_product = (int) product.getPrice() * quantity;

            if (total_price_product >= Integer.parseInt(buyyerField.getText())-1) {
                JOptionPane.showMessageDialog(this, "Kuantitas tidak boleh negatif!", "Kesalahan", JOptionPane.ERROR_MESSAGE);
            }

            // Validasi input
            if (productId <= 0 || quantity <= 0) {
                JOptionPane.showMessageDialog(this, "ID Barang dan Kuantitas harus lebih dari 0!", "Kesalahan", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Simpan transaksi ke database
            transaction transaction = new transaction(email, productId, transactionId, quantity, total_price_product, Integer.parseInt(buyyerField.getText()), new java.util.Date(), true, payment_method.getSelectedItem().toString());
            TransactionController transactionController = new TransactionController();
            if (transactionController.buyProduct(transaction)) {                
                // Proses simpan transaksi (contoh sederhana)
                JOptionPane.showMessageDialog(this, "Transaksi berhasil disimpan:\n" +
        "ID Barang: " + productId + "\nKuantitas: " + quantity+ "\n kembalian: "+ (Integer.parseInt(buyyerField.getText()) - total_price_product), "Informasi", JOptionPane.INFORMATION_MESSAGE);
            }

            // Kosongkan field
            fieldProductId.setText("");
            fieldQuantity.setText("");
            fieldEmail.setText("");
            buyyerField.setText("");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID Barang dan Kuantitas harus berupa angka!", "Kesalahan", JOptionPane.ERROR_MESSAGE);
        }

    }
}