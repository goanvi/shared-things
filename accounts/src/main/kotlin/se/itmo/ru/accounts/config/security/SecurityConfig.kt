package se.itmo.ru.accounts.config.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(
        private val filter: InternalAuthFilter
) {
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

//    @Bean
//    fun reactiveInternalAuthFilter(): ReactiveInternalAuthFilter {
//        return ReactiveInternalAuthFilter(
//                authService,
//                details
//        )
//    }

//    @Bean
//    fun authenticationManager(
//            userDetailsService: ReactiveUserDetailsService,
//            passwordEncoder: PasswordEncoder
//    ): ReactiveAuthenticationManager {
//        val authenticationManager = UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService)
//        authenticationManager.setPasswordEncoder(passwordEncoder)
//        return authenticationManager
//    }

    @Bean
    fun securityFilterChain(
            http: HttpSecurity,
    ): SecurityFilterChain {
        return http
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter::class.java)
                .csrf { it.disable() }
                .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
                .authorizeHttpRequests {
                    it.requestMatchers("/**").authenticated()
                }
                .build()
    }
}