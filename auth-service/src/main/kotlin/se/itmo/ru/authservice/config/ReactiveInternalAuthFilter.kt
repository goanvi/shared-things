//package se.itmo.ru.authservice.config
//
//import io.jsonwebtoken.JwtException
//import org.springframework.security.authentication.AbstractAuthenticationToken
//import org.springframework.security.core.authority.SimpleGrantedAuthority
//import org.springframework.security.core.context.ReactiveSecurityContextHolder
//import org.springframework.security.core.context.SecurityContextImpl
//import org.springframework.stereotype.Component
//import org.springframework.util.StringUtils
//import org.springframework.web.server.ServerWebExchange
//import org.springframework.web.server.WebFilter
//import org.springframework.web.server.WebFilterChain
//import reactor.core.publisher.Mono
//import java.util.List
//
//@Component
//class ReactiveInternalAuthenticationFilter(
//        private val jwtService: JwtService,
//) : WebFilter {
//
//    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
//        try {
//
//            val request = exchange.request
//            val headers = request.headers
//
//            val authHeader: String = headers.getFirst("Authorization") ?: return chain.filter(exchange);
//
//            val jwtToken = authHeader.substring(7);
//
//            val claims = jwtService.extract(jwtToken)
//
////            val username = claims.get("username", String::class.java)
//            val userId = claims.get("user_id", String::class.java)
//            val role = claims.get("role", String::class.java)
//
//            if (StringUtils.hasText(userId.toString())) {
//                val authentication = InternalAuthentication(userId!!, role!!)
//                val securityContext = SecurityContextImpl()
//                securityContext.authentication = authentication
//                return chain.filter(exchange)
//                        .contextWrite(
//                                ReactiveSecurityContextHolder.withSecurityContext(
//                                        Mono.just(securityContext)
//                                )
//                        )
//            }
//
//            return chain.filter(exchange)
//        } catch (e: Throwable) {
//            if (e is JwtException) {
//                return chain.filter(exchange)
//            } else {
//                throw e
//            }
//        }
//    }
//}
//
//class InternalAuthentication(
//        private val userId: String,
//        private val userRole: String
//) :
//        AbstractAuthenticationToken(List.of(SimpleGrantedAuthority(userRole))) {
//    init {
//        isAuthenticated = true
//    }
//
//    override fun getCredentials(): Any {
//        return ""
//    }
//
//    override fun getPrincipal(): String {
//        return userId
//    }
//}