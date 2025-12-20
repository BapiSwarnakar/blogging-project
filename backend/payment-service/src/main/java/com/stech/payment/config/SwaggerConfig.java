package com.stech.payment.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${server.port:9093}")
    private int serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .servers(List.of(
                    new Server().url("http://localhost:" + serverPort).description("Local Development Server")
                ))
                .components(new Components()
                    .addSecuritySchemes(securitySchemeName,
                        new SecurityScheme()
                            .name(securitySchemeName)
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT")
                            .description("JWT Authorization header using the Bearer scheme. Example: \"Authorization: Bearer {token}\"")
                    )
                )
                .info(new Info()
                    .title("Payment Service API")
                    .version("1.0")
                    .description("""
                        <h3>Payment Service API Documentation</h3>
                        <p>This is the API documentation for the Payment Service.</p>
                        
                        <h4>Authentication Flow:</h4>
                        <ol>
                            <li>Use the <strong>POST /api/v1/auth/login</strong> endpoint to authenticate</li>
                            <li>Enter your username and password in the request body</li>
                            <li>Copy the JWT token from the response</li>
                            <li>Click the <strong>Authorize</strong> button (🔒) in the top right</li>
                            <li>Enter: <code>Bearer your-jwt-token</code></li>
                            <li>Click <strong>Authorize</strong> and then <strong>Close</strong></li>
                            <li>Now you can access protected endpoints</li>
                        </ol>
                        """)
                    .license(new License()
                        .name("Apache 2.0")
                        .url("https://www.apache.org/licenses/LICENSE-2.0.html")
                    )
                );
    }

    @Bean
    public GroupedOpenApi paymentApis() {
        return GroupedOpenApi.builder()
                .group("payments")
                .displayName("Payment Management")
                .pathsToMatch("/api/v1/payments/**")
                .build();
    }
}
