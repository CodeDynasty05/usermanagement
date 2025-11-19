package com.looyt.usermanagement.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger/OpenAPI Configuration
 * Provides interactive API documentation
 */
@Configuration
public class SwaggerConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI userManagementOpenAPI() {
        Server localServer = new Server();
        localServer.setUrl("http://localhost:" + serverPort);
        localServer.setDescription("Local Development Server");

        Server productionServer = new Server();
        productionServer.setUrl("https://your-app.onrender.com");
        productionServer.setDescription("Production Server");

        Contact contact = new Contact();
        contact.setName("Looyt Team");
        contact.setEmail("support@looyt.com");
        contact.setUrl("https://looyt.com");

        License license = new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT");

        Info info = new Info()
                .title("User Management Service API")
                .version("1.0.0")
                .description("RESTful API for managing users with Kafka event streaming. " +
                        "Built with Spring Boot 3, MapStruct, EntityGraph, and Kafka.")
                .contact(contact)
                .license(license);

        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer, productionServer));
    }
}