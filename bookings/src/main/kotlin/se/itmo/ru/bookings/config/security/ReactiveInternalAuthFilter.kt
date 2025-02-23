package se.itmo.ru.bookings.config.security

import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.net.http.HttpHeaders
import java.util.List

@Component

class ReactiveInternalAuthenticationFilter : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val request = exchange.request
        val headers = request.headers

        val userId: String = headers.getFirst("X-User-Id").toString()
        val userRole: String = headers.getFirst("X-User-Role").toString()

        if (StringUtils.hasText(userId)) {
            val authentication = InternalAuthentication(userId, userRole)
            val securityContext = SecurityContextImpl()
            securityContext.authentication = authentication
            return chain.filter(exchange)
                    .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)))
        }

        return chain.filter(exchange)
    }
}

class InternalAuthentication(
        private val userId: String,
        private val userRole: String
) :
        AbstractAuthenticationToken(List.of(SimpleGrantedAuthority(userRole))) {
    init {
        isAuthenticated = true
    }

    override fun getCredentials(): Any {
        return ""
    }

    override fun getPrincipal(): String {
        return userId
    }
}