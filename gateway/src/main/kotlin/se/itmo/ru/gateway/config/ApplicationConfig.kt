package se.itmo.ru.gateway.config

import org.springframework.boot.autoconfigure.http.HttpMessageConverters
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter

@Configuration
class ApplicationConfig {

    @Bean
    fun messageConverters(): HttpMessageConverters {
        return HttpMessageConverters(MappingJackson2HttpMessageConverter())
    }
}