package se.itmo.ru.sharedthings

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SharedThingsApplication

fun main(args: Array<String>) {
  runApplication<SharedThingsApplication>(*args)
}
