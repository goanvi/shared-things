package se.itmo.ru.imageservice.config

import io.minio.MinioClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class ApplicationConfig{

    @Bean
    fun minioClient(
        @Value("\${app.minio.url}") url: String,
        @Value("\${app.minio.username}") username: String,
        @Value("\${app.minio.password}") password: String?
    ): MinioClient {
        return MinioClient.builder()
            .endpoint(url)
            .credentials(username, password)
            .build()
    }
}