package toko_online.support;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.MySQLContainer;

public class MySqlTestcontainerExtension implements BeforeAllCallback {

    private static final MySQLContainer<?> MYSQL_CONTAINER = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("retailflow_test")
            .withUsername("test")
            .withPassword("test")
            .withReuse(true);

    @Override
    public void beforeAll(ExtensionContext context) {
        if (!MYSQL_CONTAINER.isRunning()) {
            MYSQL_CONTAINER.start();
            System.setProperty("spring.datasource.url", MYSQL_CONTAINER.getJdbcUrl());
            System.setProperty("spring.datasource.username", MYSQL_CONTAINER.getUsername());
            System.setProperty("spring.datasource.password", MYSQL_CONTAINER.getPassword());
        }
    }

    public static MySQLContainer<?> getContainer() {
        return MYSQL_CONTAINER;
    }
}
