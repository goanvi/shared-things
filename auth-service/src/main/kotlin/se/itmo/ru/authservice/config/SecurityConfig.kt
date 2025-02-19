package se.itmo.ru.authservice.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers
import se.itmo.ru.authservice.services.AuthService
import se.itmo.ru.authservice.services.CustomUserDetailsService

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@Configuration
class SecurityConfig(
        private val filter: ReactiveInternalAuthenticationFilter
) {
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun authenticationManager(
            userDetailsService: ReactiveUserDetailsService,
            passwordEncoder: PasswordEncoder
    ): ReactiveAuthenticationManager {
        val authenticationManager = UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService)
        authenticationManager.setPasswordEncoder(passwordEncoder)
        return authenticationManager
    }

    @Bean
    fun securityWebFilterChain(
            http: ServerHttpSecurity,
//            filter: ReactiveInternalAuthenticationFilter
    ): SecurityWebFilterChain {
        return http
                .securityMatcher(ServerWebExchangeMatchers.pathMatchers("/users/**"))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .authorizeExchange {
                    it.pathMatchers("/error").permitAll()
                            .pathMatchers("/auth/**").permitAll()
                            .pathMatchers("/users/**").authenticated()
                }
                .addFilterBefore(filter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build()
    }
}