package se.itmo.ru.gateway.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@OpenAPIDefinition
@Configuration
class OpenAPIConfig {
    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI().info(
            Info()
                .title("API Gateway Service")
                .description("API Gateway Service")
                .version("1.0.0")
        )
    }
}