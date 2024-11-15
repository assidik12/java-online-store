package toko.online.helper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class connentionDb {
        public Connection getConnection() {
        try {
            // Load driver MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Buat koneksi ke database
            
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/java_toko_online", "root", "");

            return connection;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

}
