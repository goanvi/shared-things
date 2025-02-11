package se.itmo.ru.wishlists.rest.client

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import reactivefeign.spring.config.ReactiveFeignClient
import reactor.core.publisher.Mono
import se.itmo.ru.common.dto.request.BookingRequest
import se.itmo.ru.common.dto.response.BookingResponse
import se.itmo.ru.common.dto.response.ItemResponse
import java.util.*

@ReactiveFeignClient(name = "bookings", path = "api")
interface BookingRestClient {

    @PostMapping(value = ["/booking/create"], produces = ["application/json"], consumes = ["application/json"])
    fun createBooking(@RequestBody bookingRequest: BookingRequest): Mono<BookingResponse>

    @GetMapping(value = ["/item/{id}"], produces = ["application/json"])
    fun getItemById(@PathVariable("id") itemId: UUID): Mono<ItemResponse>
}