package config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConfig {
    private static final Logger log = LoggerFactory.getLogger(DatabaseConfig.class);
    private static HikariDataSource dataSource;

    static {
        try {
            log.info("Menginisialisasi HikariCP Connection Pool...");
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:mysql://localhost:3306/java_toko_online?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
            config.setUsername("root");
            config.setPassword("");
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");

            // HikariCP Pool Configuration
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setIdleTimeout(30000);
            config.setConnectionTimeout(30000);
            config.setMaxLifetime(1800000);

            dataSource = new HikariDataSource(config);
            log.info("HikariCP Connection Pool berhasil diinisialisasi (maxPoolSize: 10, minIdle: 2).");
        } catch (Exception e) {
            log.error("Gagal menginisialisasi database connection pool: {}", e.getMessage(), e);
            throw new RuntimeException("Gagal menginisialisasi database connection pool: " + e.getMessage(), e);
        }
    }

    private DatabaseConfig() {
        // Private constructor for utility class
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            log.error("Permintaan koneksi gagal: DataSource belum terinisialisasi.");
            throw new SQLException("DataSource belum terinisialisasi.");
        }
        return dataSource.getConnection();
    }

    public static HikariDataSource getDataSource() {
        return dataSource;
    }

    public static void closeDataSource() {
        if (dataSource != null && !dataSource.isClosed()) {
            log.info("Menutup HikariCP DataSource...");
            dataSource.close();
            log.info("HikariCP DataSource berhasil ditutup.");
        }
    }
}
