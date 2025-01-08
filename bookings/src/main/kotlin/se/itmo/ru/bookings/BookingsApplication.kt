package se.itmo.ru.bookings

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@EnableDiscoveryClient
@SpringBootApplication
class BookingsApplication

fun main(args: Array<String>) {
    runApplication<BookingsApplication>(*args)
}
