package toko_online.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

import java.util.List;

@Configuration
public class OpenApiConfig {

    private static final String BEARER_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI tokoOnlineOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Toko Online REST API")
                        .version("1.0.0")
                        .description("REST API untuk Toko Online & POS dengan Spring Boot 3, Spring Security, JWT, dan RBAC.")
                        .contact(new Contact()
                                .name("Toko Online Dev Team")
                                .email("dev@toko-online.local"))
                        .license(new License()
                                .name("Proprietary")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local Development")))
                .components(new Components()
                        .addSecuritySchemes(BEARER_SCHEME_NAME, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Masukkan JWT access token yang diperoleh dari /api/v1/auth/login.")))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME_NAME));
    }
}
