package view;

import controller.TransactionController;
import model.dto.response.ApiResponse;
import model.dto.response.TransactionResponse;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.text.SimpleDateFormat;
import java.util.List;

public class TransctionView extends JFrame {
    private JTable transactionTable;
    private DefaultTableModel tableModel;
    private final TransactionController transactionController;
    private JTextField emailField;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public TransctionView() {
        this.transactionController = new TransactionController();

        setTitle("Histori Transaksi");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(850, 550);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Kolom tabel
        String[] columnNames = {
                "ID Transaksi", "Email Pengguna", "Total Belanja",
                "Status", "Tanggal", "Metode Pembayaran", "Item"
        };

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        transactionTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(transactionTable);
        add(scrollPane, BorderLayout.CENTER);

        // Panel filter email
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel emailLabel = new JLabel("Filter Email Pengguna:");
        emailField = new JTextField(20);
        JButton filterButton = new JButton("Cari");
        filterButton.addActionListener(e -> showTransactions());

        inputPanel.add(emailLabel);
        inputPanel.add(emailField);
        inputPanel.add(filterButton);

        add(inputPanel, BorderLayout.NORTH);

        // Tombol refresh/tampilkan semua
        JButton showAllButton = new JButton("Tampilkan Semua Transaksi");
        showAllButton.addActionListener(e -> {
            emailField.setText("");
            showTransactions();
        });
        add(showAllButton, BorderLayout.SOUTH);

        // Muat data awal
        showTransactions();
    }

    private void showTransactions() {
        tableModel.setRowCount(0);
        String email = emailField.getText().trim();

        ApiResponse<List<TransactionResponse>> response = transactionController.getTransactions(email);
        if (response.isSuccess() && response.getData() != null) {
            for (TransactionResponse tx : response.getData()) {
                String itemsSummary = (tx.getDetails() != null && !tx.getDetails().isEmpty())
                        ? tx.getDetails().stream()
                            .map(d -> d.getProductName() + " (x" + d.getQuantity() + ")")
                            .reduce((a, b) -> a + ", " + b).orElse("-")
                        : "-";

                Object[] row = {
                        tx.getTransactionId(),
                        tx.getUserEmail(),
                        "Rp. " + tx.getFormattedTotalPriceAmount(),
                        tx.getStatus() != null ? tx.getStatus().name() : "PAID",
                        tx.getDate() != null ? dateFormat.format(tx.getDate()) : "-",
                        tx.getPaymentMethod() != null ? tx.getPaymentMethod().getDisplayName() : "Cash",
                        itemsSummary
                };
                tableModel.addRow(row);
            }
        } else {
            JOptionPane.showMessageDialog(this, response.getMessage(), "Gagal Memuat Transaksi", JOptionPane.ERROR_MESSAGE);
        }
    }
}