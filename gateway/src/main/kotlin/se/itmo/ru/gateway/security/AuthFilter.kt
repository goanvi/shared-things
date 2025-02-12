package se.itmo.ru.gateway.security

import feign.FeignException.FeignClientException
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.context.annotation.Lazy
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import se.itmo.ru.common.dto.request.auth.ValidateTokenRequestDto
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory as AbstractGatewayFilterFactory

@Component
class AuthFilter(
        private val validator: RouteValidator,
        @Lazy
        private val authServiceClient: AuthServiceClient
) : AbstractGatewayFilterFactory<AuthFilter.Config>(Config::class.java) {


    override fun apply(config: Config?): GatewayFilter {
        return GatewayFilter { exchange: ServerWebExchange, chain: GatewayFilterChain ->
            if (validator.isSecured.test(exchange.request)) {
                if (!exchange.request.headers.containsKey(HttpHeaders.AUTHORIZATION)) {
                    throw ResponseStatusException(
                            HttpStatus.UNAUTHORIZED,
                            "Missing authorization header"
                    )
                }

                val authHeaderValue = exchange.request.headers[HttpHeaders.AUTHORIZATION]?.get(0)
                if (authHeaderValue == null || !authHeaderValue.startsWith("Bearer ")) {
                    throw ResponseStatusException(
                            HttpStatus.UNAUTHORIZED,
                            "Missing authorization header"
                    )
                }

                val token = authHeaderValue.substring(7)
                Mono.fromCallable {
                    authServiceClient.validate(
                            ValidateTokenRequestDto(token)
                    )
                }
                        .flatMap { user ->

                            val modifiedRequest = exchange.request.mutate()
                                    .header("X-User-Id", user?.userId.toString())
                                    .header("X-User-Role", user?.userRoleDto.toString())
                                    .build()
                            val modifiedExchange = exchange.mutate()
                                    .request(modifiedRequest)
                                    .build()
                            chain.filter(modifiedExchange)
                        }.onErrorResume { e ->
                            if (e.cause is FeignClientException) {
                                Mono.error<Throwable>(ResponseStatusException(
                                        HttpStatus.UNAUTHORIZED,
                                        "Missing authorization header 123123123"
                                ))
                            }
                            Mono.error(e)
                        }
            }
            chain.filter(exchange)
        }
    }

    class Config

}


