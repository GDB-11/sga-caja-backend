package com.cibertec.sga.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración base de Swagger/OpenAPI ({@code springdoc-openapi}), expuesta en
 * {@code /swagger-ui.html}.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI sgaOpenApi() {
        return new OpenAPI().info(
            new Info()
                .title("SGA Caja API")
                .description("API REST del Sistema de Gestión Administrativa y de Caja")
                .version("v0")
        );
    }
}
