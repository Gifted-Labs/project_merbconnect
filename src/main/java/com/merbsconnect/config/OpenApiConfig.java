package com.merbsconnect.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI/Swagger Configuration for MerbsConnect API
 * Provides global API documentation, security definitions, and server configurations.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "MerbsConnect API",
                version = "1.0.0",
                description = "Comprehensive API for MerbsConnect - A Management and Resource Booking System for Educational Institutes. " +
                        "This API provides endpoints for user authentication, academic management, resource booking, and more.",
                contact = @Contact(
                        name = "MerbsConnect Support",
                        email = "support@merbsconnect.com",
                        url = "https://www.merbsconnect.com"
                )
        ),
        servers = {
                @Server(
                        url = "http://localhost:8080",
                        description = "Local Development Server"
                ),
                @Server(
                        url = "https://api.merbsconnect.com",
                        description = "Production Server"
                )
        }
)
@SecurityScheme(
        name = "Bearer Authentication",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "JWT authentication token. Include the token in the Authorization header as 'Bearer <token>'"
)
public class OpenApiConfig {
    // This class serves as a configuration holder for OpenAPI/Swagger documentation
}

