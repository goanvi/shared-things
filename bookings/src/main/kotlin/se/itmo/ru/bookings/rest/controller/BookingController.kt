package se.itmo.ru.bookings.rest.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import se.itmo.ru.common.dto.request.BookingRequest
import se.itmo.ru.common.dto.response.BookingResponse
import se.itmo.ru.bookings.service.BookingService
import se.itmo.ru.common.dto.AccountDto
import java.util.*

@RestController
@RequestMapping("booking")
@Tag(name = "Бронирование")
class BookingController(
    private val service: BookingService
) {

    @Operation(
        summary = "Создание бронирования на предметы",
        description = "Позволяет создать бронирование на предметы"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "Бронирование создано",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = BookingResponse::class)
                )]
            ),
            ApiResponse(responseCode = "400", description = "Некорректный запрос"),
            ApiResponse(responseCode = "422", description = "Некореектные данные")
        ]
    )
    @PostMapping("/create")
    fun createBooking(
        @Valid @RequestBody bookingRequest: BookingRequest
    ): Mono<BookingResponse> =
        service.createBooking(bookingRequest)

    @Operation(
        summary = "Завершить бронирования по id",
        description = "Позволяет завершить бронирование по id"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "Бронирование закрыто",
            ),
            ApiResponse(responseCode = "400", description = "Некорректный запрос"),
            ApiResponse(responseCode = "422", description = "Некореектные данные")
        ]
    )
    @PostMapping("/close/{id}")
    fun closeBooking(@PathVariable("id") bookingId: UUID): Mono<Void> =
        service.closeBooking(bookingId)

    @Operation(
        summary = "Просмотр бронирования по id",
        description = "Просмотр бронирования по id"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "Бронирование",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = BookingResponse::class)
                )]
            ),
            ApiResponse(responseCode = "400", description = "Некорректный запрос"),
            ApiResponse(responseCode = "422", description = "Некореектные данные")
        ]
    )
    @GetMapping("/{id}")
    fun getBookingById(@PathVariable("id") bookingId: UUID): Mono<BookingResponse> =
        service.getBookingById(bookingId)

}