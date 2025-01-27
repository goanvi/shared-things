package se.itmo.ru.bookings.dto.response

import se.itmo.ru.bookings.enum.BookingStatus
import java.time.LocalDateTime
import java.util.*

data class BookingResponse(
    var bookingId: UUID,

    //Account
    var renter: UUID,

    var startDate: LocalDateTime,

    val endDate: LocalDateTime,

    var status: BookingStatus,

    val description: String? = null,

    val bookedItems: Set<UUID>,
)
