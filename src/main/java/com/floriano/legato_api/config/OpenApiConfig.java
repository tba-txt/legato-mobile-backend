package com.floriano.legato_api.config;


import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;

@OpenAPIDefinition(
        info = @Info(
                title = "LEGATO - OpenAPI Specification",
                description = "API for serving the Legato plataform "
                        + "Provides endpoints to explore the history of philosophy and its .",
                version = "1.0",
                contact = @Contact(
                        name = "Ernesto Floriano Amorim",
                        email = "ernestofamorim@gmail.com",
                        url = "https://github.com/Erne1984"
                ),
                license = @License(
                        name = "MIT License",
                        url = "https://opensource.org/licenses/MIT"
                )
        ),
        servers = {
                @Server(
                        description = "Local Environment",
                        url = "http://localhost:8081/"
                ),
                @Server(
                        description = "Production Environment",
                        url = "TO DO YET"
                )
        },
        tags = {
                @Tag(name = "Users", description = "Endpoints related to users"),
                @Tag(name = "Notifications", description = "Endpoints related to notifications"),
                @Tag(name = "Collaborations", description = "Endpoints related to collaborations"),
                @Tag(name = "Posts", description = "Endpoints related to posts"),
                @Tag(name = "Comments", description = "Endpoints related to comments"),
                @Tag(name = "Auth", description = "Endpoints related to authentication and registration"),
        },
        externalDocs = @ExternalDocumentation(
                description = "Full documentation, request/response examples, and usage guides",
                url = ""
        )
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT Authentication using the Bearer scheme. "
                + "Include the JWT token in the Authorization header: Bearer {token}",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}