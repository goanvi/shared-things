package se.itmo.ru.authservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import reactivefeign.spring.config.EnableReactiveFeignClients

@SpringBootApplication
@EnableDiscoveryClient
@EnableReactiveFeignClients
class AuthServiceApplication

fun main(args: Array<String>) {
    runApplication<AuthServiceApplication>(*args)
}
