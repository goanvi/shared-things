package se.itmo.ru.sharedthings

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.web.config.EnableSpringDataWebSupport

@SpringBootApplication
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
class SharedThingsApplication

fun main(args: Array<String>) {
    runApplication<SharedThingsApplication>(*args)
}
