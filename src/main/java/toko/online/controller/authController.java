package toko.online.controller;

import toko.online.helper.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import toko.online.model.user;

public class authController {
    Connection connection = new connentionDb().getConnection();

    public boolean RegisterController(user user) {
    try {
        System.out.println(user.getUsername());
        PreparedStatement statement = connection.prepareStatement("INSERT INTO user (username, password, email, role, phone_number, address, pos_code) VALUES (?, ?, ?, ?, ?, ?, ?)");
        statement.setString(1, user.getUsername());
        statement.setString(2, user.getPassword()); // Pertimbangkan untuk meng-hash password
        statement.setString(3, user.getEmail());
        statement.setString(4, "user");
        statement.setString(5, user.getPhoneNumber());
        statement.setString(6, user.getAddress());
        statement.setString(7, user.posCode());
        statement.executeUpdate();
        // Tampilkan pesan sukses
        return true;
        }
     catch (SQLException e) {
        // Tampilkan pesan error
        throw new RuntimeException(e.getErrorCode() + ": " + e.getMessage());
    }
    // return false;
    }
}
