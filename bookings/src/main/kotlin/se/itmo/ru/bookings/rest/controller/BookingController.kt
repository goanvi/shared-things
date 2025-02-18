package se.itmo.ru.bookings.rest.controller

import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import se.itmo.ru.common.dto.request.BookingRequest
import se.itmo.ru.common.dto.response.BookingResponse
import se.itmo.ru.bookings.service.BookingService
import java.util.*

@RestController
@RequestMapping("booking")
class BookingController(
    private val service: BookingService
) {

    @PostMapping("/create")
    fun createBooking(
        @Valid @RequestBody bookingRequest: BookingRequest
    ): Mono<BookingResponse> =
        service.createBooking(bookingRequest)

    @PostMapping("/close/{id}")
    fun closeBooking(@PathVariable("id") bookingId: UUID): Mono<Void> =
        service.closeBooking(bookingId)

    @GetMapping("/{id}")
    fun getBookingById(@PathVariable("id") bookingId: UUID): Mono<BookingResponse> =
        service.getBookingById(bookingId)

}