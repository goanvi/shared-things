package se.itmo.ru.security.reactive

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
open class ReactiveSecurityConfig {

    @Bean
    open fun reactiveInternalAuthFilter(): ReactiveInternalAuthFilter {
        return ReactiveInternalAuthFilter()
    }

    @Bean
    open fun reactiveFeignClientInterceptor(): ReactiveFeignClientInterceptor {
        return ReactiveFeignClientInterceptor()
    }
}