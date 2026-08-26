package view;

import controller.ProductController;
import controller.TransactionController;
import model.dto.request.TransactionRequest;
import model.dto.response.ApiResponse;
import model.dto.response.ProductResponse;
import model.dto.response.TransactionResponse;
import model.enums.PaymentMethod;

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
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;

public class ProductView extends JFrame {
    private JTable productTable;
    private DefaultTableModel tableModel;
    private final ProductController productController;
    private final TransactionController transactionController;

    private JTextField fieldProductId;
    private JTextField fieldQuantity;
    private JTextField fieldEmail;
    private JTextField buyerField;
    private JComboBox<PaymentMethod> paymentMethodComboBox;

    public ProductView() {
        this.productController = new ProductController();
        this.transactionController = new TransactionController();

        setTitle("Data Barang & Kasir");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(650, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Kolom tabel
        String[] columnNames = { "ID Barang", "Nama Barang", "Harga", "Stok" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        productTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(productTable);
        add(scrollPane, BorderLayout.CENTER);

        loadProducts();

        // Panel input transaksi
        JPanel transactionPanel = new JPanel();
        transactionPanel.setBorder(BorderFactory.createTitledBorder("Input Transaksi Pembelian"));
        transactionPanel.setLayout(new BoxLayout(transactionPanel, BoxLayout.Y_AXIS));

        fieldProductId = new JTextField();
        fieldQuantity = new JTextField();
        fieldEmail = new JTextField();
        buyerField = new JTextField();
        paymentMethodComboBox = new JComboBox<>(PaymentMethod.values());

        JPanel formGrid = new JPanel(new GridLayout(5, 2, 5, 5));
        formGrid.add(new JLabel("ID Barang:"));
        formGrid.add(fieldProductId);

        formGrid.add(new JLabel("Kuantitas:"));
        formGrid.add(fieldQuantity);

        formGrid.add(new JLabel("Email Pembeli:"));
        formGrid.add(fieldEmail);

        formGrid.add(new JLabel("Jumlah Uang (Rp):"));
        formGrid.add(buyerField);

        formGrid.add(new JLabel("Metode Pembayaran:"));
        formGrid.add(paymentMethodComboBox);

        transactionPanel.add(formGrid);
        transactionPanel.add(Box.createVerticalStrut(10));

        JButton saveTransactionButton = new JButton("Proses Transaksi");
        saveTransactionButton.addActionListener(e -> saveTransaction());
        transactionPanel.add(saveTransactionButton);

        add(transactionPanel, BorderLayout.SOUTH);
    }

    public void loadProducts() {
        tableModel.setRowCount(0);
        ApiResponse<List<ProductResponse>> response = productController.findProducts();
        if (response.isSuccess() && response.getData() != null) {
            for (ProductResponse p : response.getData()) {
                Object[] row = {
                        p.getId(),
                        p.getName(),
                        p.getFormattedPrice(),
                        p.getStock()
                };
                tableModel.addRow(row);
            }
        } else {
            JOptionPane.showMessageDialog(this, response.getMessage(), "Gagal Memuat Produk", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveTransaction() {
        try {
            if (fieldProductId.getText().trim().isEmpty() ||
                fieldQuantity.getText().trim().isEmpty() ||
                fieldEmail.getText().trim().isEmpty() ||
                buyerField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Semua field harus diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int productId = Integer.parseInt(fieldProductId.getText().trim());
            int quantity = Integer.parseInt(fieldQuantity.getText().trim());
            String email = fieldEmail.getText().trim();
            int paidAmount = Integer.parseInt(buyerField.getText().trim());
            PaymentMethod paymentMethod = (PaymentMethod) paymentMethodComboBox.getSelectedItem();

            TransactionRequest request = new TransactionRequest(productId, quantity, email, paidAmount, paymentMethod);
            ApiResponse<TransactionResponse> response = transactionController.buyProduct(request);

            if (response.isSuccess()) {
                TransactionResponse tx = response.getData();
                String info = String.format("Transaksi Berhasil!\n\n" +
                                "ID Transaksi: %s\n" +
                                "Total Belanja: Rp. %,d\n" +
                                "Uang Dibayar: Rp. %,d\n" +
                                "Kembalian: Rp. %,d\n" +
                                "Metode: %s",
                        tx.getTransactionId(),
                        tx.getTotalPriceAmount(),
                        tx.getPaidAmount(),
                        tx.getChangeAmount(),
                        tx.getPaymentMethod().getDisplayName()
                );
                JOptionPane.showMessageDialog(this, info, "Sukses", JOptionPane.INFORMATION_MESSAGE);

                // Reset form fields
                fieldProductId.setText("");
                fieldQuantity.setText("");
                fieldEmail.setText("");
                buyerField.setText("");

                // Refresh product stock list
                loadProducts();
            } else {
                JOptionPane.showMessageDialog(this, response.getMessage(), "Transaksi Gagal", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID Barang, Kuantitas, dan Jumlah Uang harus berupa angka bulat!", "Kesalahan Input", JOptionPane.ERROR_MESSAGE);
        }
    }
}