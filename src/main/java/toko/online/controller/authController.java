package toko.online.controller;

import toko.online.helper.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class authController {
        Connection connection = new connentionDb().getConnection();

    public boolean RegisterController(String username, String password, String email) {

    try {
        PreparedStatement statement = connection.prepareStatement("INSERT INTO user (username, password, email) VALUES (?, ?, ?)");
        statement.setString(1, username);
        statement.setString(2, password); // Pertimbangkan untuk meng-hash password
        statement.setString(3, email);
        statement.executeUpdate();
        // Tampilkan pesan sukses
        System.out.println("Register success");
    } catch (SQLException e) {
        // Tampilkan pesan error
        throw new RuntimeException(e.getErrorCode() + ": " + e.getMessage());
    }
        return false;
    }

}
