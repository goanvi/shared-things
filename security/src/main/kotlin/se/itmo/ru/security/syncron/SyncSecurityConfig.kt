package se.itmo.ru.security.syncron

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class SyncSecurityConfig {
    @Bean
    open fun internalAuthFilter(): InternalAuthFilter {
        return InternalAuthFilter()
    }

}