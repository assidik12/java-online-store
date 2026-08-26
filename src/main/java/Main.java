import view.ProductView;
import view.TransctionView;
import view.authView;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;

public class Main extends JFrame {

    public Main() {
        setTitle("Toko Online");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 320);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Panel Header
        JPanel headerPanel = new JPanel();
        JLabel welcomeLabel = new JLabel("Selamat Datang di Toko Online");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerPanel.add(welcomeLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Panel Menu
        JPanel menuPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        JButton daftarButton = new JButton("1. Registrasi Akun");
        JButton lihatBarangButton = new JButton("2. Lihat Barang & Transaksi");
        JButton riwayatButton = new JButton("3. Riwayat Transaksi");

        daftarButton.setFont(new Font("Arial", Font.BOLD, 14));
        lihatBarangButton.setFont(new Font("Arial", Font.BOLD, 14));
        riwayatButton.setFont(new Font("Arial", Font.BOLD, 14));

        menuPanel.add(daftarButton);
        menuPanel.add(lihatBarangButton);
        menuPanel.add(riwayatButton);

        add(menuPanel, BorderLayout.CENTER);

        // Event Listeners
        daftarButton.addActionListener(e -> showMenu(1));
        lihatBarangButton.addActionListener(e -> showMenu(2));
        riwayatButton.addActionListener(e -> showMenu(3));

        setVisible(true);
    }

    private void showMenu(int menu) {
        if (menu == 1) {
            SwingUtilities.invokeLater(() -> {
                authView auth = new authView();
                auth.RegisterView();
            });
        } else if (menu == 2) {
            SwingUtilities.invokeLater(() -> {
                ProductView gui = new ProductView();
                gui.setVisible(true);
            });
        } else if (menu == 3) {
            SwingUtilities.invokeLater(() -> {
                TransctionView gui = new TransctionView();
                gui.setVisible(true);
            });
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        SwingUtilities.invokeLater(Main::new);
    }
}
