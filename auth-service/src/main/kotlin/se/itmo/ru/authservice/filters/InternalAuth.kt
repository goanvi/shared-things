package se.itmo.ru.authservice.filters

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.util.List


class InternalAuth(
        authorities: MutableCollection<out GrantedAuthority>?
) : AbstractAuthenticationToken(authorities) {
    private var userId: String? = null
    private var userRole: String? = null

    constructor(userId: String?, role: String?) : this(List.of(SimpleGrantedAuthority(role))) {
        this.userId = userId
        this.userRole = role
        setAuthenticated(true)
    }

    override fun getCredentials(): Any {
        return ""
    }

    override fun getPrincipal(): String? {
        return userId
    }
}