package toko_online;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration;

@SpringBootApplication(scanBasePackages = {
        "toko_online",
        "controller",
        "service",
        "repository",
        "exception"
}, exclude = { R2dbcAutoConfiguration.class })
public class TokoOnlineApplication {

    public static void main(String[] args) {
        SpringApplication.run(TokoOnlineApplication.class, args);
    }
}
