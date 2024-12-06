package toko.online.view;



import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.awt.*;

import at.favre.lib.crypto.bcrypt.BCrypt;
import toko.online.controller.authController;
import toko.online.model.user;



public class authView {

    public void RegisterView() {
        // Membuat frame utama
        JFrame frame = new JFrame("Registration Form");
        frame.setSize(400, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Panel utama
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(8, 2, 10, 10));
        frame.add(panel);

        // Label dan Text Field
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

        // Tombol Submit
        JButton submitButton = new JButton("Submit");
        JLabel resultLabel = new JLabel();

        // Menambahkan komponen ke panel
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
        panel.add(new JLabel()); // Placeholder
        panel.add(submitButton);
        panel.add(new JLabel()); // Placeholder
        panel.add(resultLabel);

        // Action Listener untuk tombol submit
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                String bcryptHashString = BCrypt.withDefaults().hashToString(12, password.toCharArray());
                String email = emailField.getText();
                String phoneNumber = phoneField.getText();
                String address = addressField.getText();
                String posCode = posCodeField.getText();

                user user = new user(username, bcryptHashString, email);
                user.address(phoneNumber, address, posCode);

                authController authController = new authController();
                authController.RegisterController(user);

                resultLabel.setText("Data saved!");
            }
        });
        // Menampilkan frame
        frame.setVisible(true);   
     }

}
