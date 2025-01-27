package se.itmo.ru.bookings.dto.request

import jakarta.validation.constraints.Future
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime
import java.util.*

data class BookingRequest(

    @field:NotNull
    val renter: UUID,

    @field:NotNull
    @field:Future
    val endDate: LocalDateTime,

    val description: String?,

    @field:NotNull(message = "items cannot be null")
    val bookedItems: Set<UUID>,
)
