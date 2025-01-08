package se.itmo.ru.wishlists

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@EnableDiscoveryClient
@SpringBootApplication
class WishlistsApplication

fun main(args: Array<String>) {
    runApplication<WishlistsApplication>(*args)
}
