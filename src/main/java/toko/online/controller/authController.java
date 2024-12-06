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
        PreparedStatement statement = connection.prepareStatement("INSERT INTO user (username, password, email) VALUES (?, ?, ?)");
        statement.setString(1, user.getUsername());
        statement.setString(2, user.getPassword()); // Pertimbangkan untuk meng-hash password
        statement.setString(3, user.getEmail());
        if (statement.executeUpdate()>0) {
            if (insertAddress(user)) {
                System.out.println("Register success");
                return true;
            }
        }
        // Tampilkan pesan sukses
    } catch (SQLException e) {
        // Tampilkan pesan error
        throw new RuntimeException(e.getErrorCode() + ": " + e.getMessage());
    }
        return false;
    }

    boolean insertAddress(user user) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("INSERT INTO address (user_email, phone_number, address, pos_code) VALUES (?, ?, ?, ?)");
        statement.setString(1, user.getEmail());
        statement.setString(2, user.getPhoneNumber());
        statement.setString(3, user.getAddress());
        statement.setString(4, user.posCode());

        if (statement.executeUpdate()>0)
            return true;
        else
            return false;
        
    }
}
