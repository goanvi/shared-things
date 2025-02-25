package se.itmo.ru.accounts.config.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers
import org.springframework.util.PathMatcher
import se.itmo.ru.security.syncron.InternalAuthFilter
import se.itmo.ru.security.syncron.SyncSecurityConfig


@Import(SyncSecurityConfig::class)
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig {
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        filter: InternalAuthFilter
    ): SecurityFilterChain {
        return http
            .csrf { it.disable() }
            .authorizeHttpRequests {
                it.requestMatchers("/account/**").authenticated()
                    .requestMatchers(
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
            }
            .addFilterBefore(filter, UsernamePasswordAuthenticationFilter::class.java)
            .build()
    }
}