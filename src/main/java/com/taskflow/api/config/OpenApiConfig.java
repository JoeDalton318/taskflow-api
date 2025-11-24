package com.taskflow.api.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "TaskFlow API",
        version = "1.0.0",
        description = "API de gestion de tâches collaborative avec authentification JWT",
        contact = @Contact(
            name = "TaskFlow Team",
            email = "contact@taskflow.com"
        )
    ),
    servers = {
        @Server(url = "http://localhost:8081", description = "Serveur de développement"),
        @Server(url = "https://taskflow-api.onrender.com", description = "Serveur de production")
    }
)
@SecurityScheme(
    name = "bearer-jwt",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
