package toko.online;

import java.awt.*;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import toko.online.view.ProductView;
import toko.online.view.TransctionView;
import toko.online.view.authView;


public class Main extends JFrame {

     public Main() {
        // Pengaturan JFrame
        setTitle("Toko Online");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLayout(new BorderLayout());

        // Panel Header
        JPanel headerPanel = new JPanel();
        JLabel welcomeLabel = new JLabel("Selamat Datang di Toko Online");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(welcomeLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Panel Menu
        JPanel menuPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        JButton daftarButton = new JButton("1. Daftar");
        JButton lihatBarangButton = new JButton("2. Lihat Barang");
        JButton transaksiButton = new JButton("3. Transaksi");

        menuPanel.add(daftarButton);
        menuPanel.add(lihatBarangButton);
        menuPanel.add(transaksiButton);

        // Tambahkan Panel Menu ke JFrame
        add(menuPanel, BorderLayout.CENTER);

        // Event Listener
        daftarButton.addActionListener(e -> showMessage(1));
        lihatBarangButton.addActionListener(e -> showMessage(2));
        transaksiButton.addActionListener(e -> showMessage(3));

        setVisible(true);
    }

    private void showMessage(Integer menu) {
        if (menu == 1) {
            authView auth = new authView();
            auth.RegisterView();
        } else if (menu == 2) {
            SwingUtilities.invokeLater(() -> {
            ProductView gui = new ProductView();
            gui.setVisible(true);
        });
        } else if (menu == 3) {
            SwingUtilities.invokeLater(
                () -> {
                    TransctionView gui = new TransctionView();
                    gui.setVisible(true);
                }
            );
        }
    }

    public static void main(String[] args) throws IOException {
        new Main();
    }
    
 
}
