package se.itmo.ru.bookings.config.security

import io.jsonwebtoken.JwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter
import java.util.List

@Component
class InternalAuthFilter(
        private val jwtService: JwtService,
) : OncePerRequestFilter() {
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        try {

            val authHeader = request.getHeader("Authorization") ?: return filterChain.doFilter(request, response)

            val jwtToken = authHeader.substring(7);
            val claims = jwtService.extract(jwtToken)

            val userId = claims.get("user_id", String::class.java)
            val role = claims.get("role", String::class.java)

            if (StringUtils.hasText(userId)) {
                val authentication = InternalAuthentication(userId, role)
                SecurityContextHolder.getContext().authentication = authentication
            }

            return filterChain.doFilter(request, response)
        } catch (e: Throwable) {
            if (e is JwtException) {
                return filterChain.doFilter(request, response)
            } else {
                throw e
            }
        }
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