package se.itmo.ru.bookings.controller

import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*
import se.itmo.ru.bookings.dto.BookingDto
import se.itmo.ru.bookings.service.BookingService

@RestController
@RequestMapping("api/booking")
class BookingController(
    private val service: BookingService
) {

    @PostMapping("/create")
    fun createBooking(
        @RequestParam("renterId") renterId: Int,
        @Valid @RequestBody bookingDto: BookingDto
    ): BookingDto =
        service.createBooking(renterId, bookingDto)

    @PostMapping("/close/{id}")
    fun closeBooking(@PathVariable("id") bookingId: Int): BookingDto =
        service.closeBooking(bookingId)

    @GetMapping("/{id}")
    fun getBookingById(@PathVariable("id") bookingId: Int): BookingDto =
        service.getBookingById(bookingId)

}