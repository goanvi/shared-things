package se.itmo.ru.security.common

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.util.*
import java.util.List

class InternalAuth(
         val userId: UUID,
         val userRole: String
) :
        AbstractAuthenticationToken(List.of(SimpleGrantedAuthority(userRole))) {
    init {
        isAuthenticated = true
    }

    override fun getCredentials(): Any {
        return ""
    }

    override fun getPrincipal(): UUID {
        return userId
    }
}