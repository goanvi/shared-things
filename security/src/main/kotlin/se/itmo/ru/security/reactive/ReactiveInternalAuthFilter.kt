package se.itmo.ru.security.reactive

import org.springframework.http.HttpHeaders
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.util.StringUtils
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import se.itmo.ru.security.common.InternalAuth
import java.util.*


class ReactiveInternalAuthFilter : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {

        val request: ServerHttpRequest = exchange.request
        val headers: HttpHeaders = request.headers

        val userId: String = headers.getFirst("X-User-Id")?.toString() ?: ""
        val userRole: String = headers.getFirst("X-User-Role")?.toString() ?: ""

        if (StringUtils.hasText(userId)) {
            val authentication = InternalAuth(UUID.fromString(userId), userRole)
            val securityContext = SecurityContextImpl()
            securityContext.authentication = authentication
            return chain.filter(exchange)
                    .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)))
        }

        return chain.filter(exchange)
    }
}