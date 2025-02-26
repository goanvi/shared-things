package se.itmo.ru.authservice.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AppConfig {

    @Value("\${api.server.url}")
    private val apiServerUrl: String? = null


    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
            .servers(listOf(Server().url(apiServerUrl)))
            .info(
                Info()
                    .title("Authentication service API")
                    .description("Authentication Service API Specs")
                    .version("1.0.0")
            )
            .components(
                Components()
                    .addSecuritySchemes(
                        "bearer-key",
                        SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")
                    )
            );
    }
}