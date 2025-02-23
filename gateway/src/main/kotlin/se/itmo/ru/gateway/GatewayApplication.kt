package se.itmo.ru.gateway

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import reactivefeign.spring.config.EnableReactiveFeignClients
import se.itmo.ru.gateway.security.AuthServiceClient

@EnableReactiveFeignClients(clients = [AuthServiceClient::class])
@EnableDiscoveryClient
@SpringBootApplication
class GatewayApplication

fun main(args: Array<String>) {
    runApplication<GatewayApplication>(*args)
}
