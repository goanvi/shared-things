package se.itmo.ru.bookings.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository
import se.itmo.ru.security.reactive.ReactiveInternalAuthFilter
import se.itmo.ru.security.reactive.ReactiveSecurityConfig


@Import(ReactiveSecurityConfig::class)
@EnableReactiveMethodSecurity
@EnableWebFluxSecurity
@Configuration
class SecurityConfig {

    @Bean
    fun securityWebFilterChain(
        http: ServerHttpSecurity,
        filter: ReactiveInternalAuthFilter

    ): SecurityWebFilterChain {

        return http
            .csrf { it.disable() }
            .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())

            .addFilterAt(filter, SecurityWebFiltersOrder.AUTHENTICATION)

            .authorizeExchange { authorizeExchangeSpec ->
                authorizeExchangeSpec
                    .pathMatchers(
                        "/swagger-ui/**",
                        "/api/v1/user-management/auth-info",
                        "/v2/api-docs",
                        "/swagger-resources",
                        "/swagger-resources/**",
                        "/configuration/ui",
                        "/configuration/security",
                        "/swagger-ui.html",
                        "/webjars/**",
                        "/v3/api-docs/**",
                        "/error/**",
                        "/favicon.ico",
                        "/error",
                        "/api/auth/**"
                    ).permitAll()
                    .pathMatchers("/**").authenticated()
            }
            .build()

    }

}