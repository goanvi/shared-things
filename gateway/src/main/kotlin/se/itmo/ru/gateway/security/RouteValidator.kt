package se.itmo.ru.gateway.security

import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Component
import java.util.function.Predicate

@Component
class RouteValidator {

    val openApiEndpoints: List<String> = listOf(
            "/auth/login"
    )

    var isSecured: Predicate<ServerHttpRequest> = Predicate<ServerHttpRequest> { request ->
        openApiEndpoints
                .stream()
                .noneMatch { uri -> request.getURI().getPath().contains(uri) }
    }
}