package view;

import controller.AuthController;
import model.dto.request.RegisterRequest;
import model.dto.response.ApiResponse;
import model.dto.response.UserResponse;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class authView {

    private final AuthController authController;

    public authView() {
        this.authController = new AuthController();
    }

    public void RegisterView() {
        JFrame frame = new JFrame("Registration Form");
        frame.setSize(400, 500);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(8, 2, 10, 10));
        frame.add(panel);

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField();

        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();

        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField();

        JLabel phoneLabel = new JLabel("Phone Number:");
        JTextField phoneField = new JTextField();

        JLabel addressLabel = new JLabel("Address:");
        JTextField addressField = new JTextField();

        JLabel posCodeLabel = new JLabel("Pos Code:");
        JTextField posCodeField = new JTextField();

        JButton submitButton = new JButton("Submit");
        JLabel resultLabel = new JLabel();

        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(emailLabel);
        panel.add(emailField);
        panel.add(phoneLabel);
        panel.add(phoneField);
        panel.add(addressLabel);
        panel.add(addressField);
        panel.add(posCodeLabel);
        panel.add(posCodeField);
        panel.add(new JLabel());
        panel.add(submitButton);
        panel.add(new JLabel());
        panel.add(resultLabel);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                String email = emailField.getText();
                String phoneNumber = phoneField.getText();
                String address = addressField.getText();
                String posCode = posCodeField.getText();

                RegisterRequest request = new RegisterRequest(username, password, email, phoneNumber, address, posCode);
                ApiResponse<UserResponse> response = authController.register(request);

                if (response.isSuccess()) {
                    JOptionPane.showMessageDialog(frame, "Registrasi berhasil untuk user: " + response.getData().getUsername(), "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    resultLabel.setText("Data tersimpan!");
                    frame.dispose();
                } else {
                    JOptionPane.showMessageDialog(frame, response.getMessage(), "Kesalahan Registrasi", JOptionPane.ERROR_MESSAGE);
                    resultLabel.setText("Registrasi gagal.");
                }
            }
        });

        frame.setVisible(true);
    }
}
