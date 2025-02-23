package se.itmo.ru.imageservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@SpringBootApplication
@EnableDiscoveryClient
class ImageServiceApplication

fun main(args: Array<String>) {
	runApplication<ImageServiceApplication>(*args)
}
