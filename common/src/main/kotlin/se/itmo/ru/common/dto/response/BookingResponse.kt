package se.itmo.ru.common.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import se.itmo.ru.common.BookingStatus
import java.time.LocalDateTime
import java.util.*

data class BookingResponse(
    @field:JsonProperty("booking_id")
    var bookingId: UUID,

    //Account
    var renter: UUID,

    @field:JsonProperty("start_date")
    var startDate: LocalDateTime,

    @field:JsonProperty("end_date")
    val endDate: LocalDateTime,

    var status: BookingStatus,

    val description: String? = null,

    @field:JsonProperty("booked_items")
    val bookedItems: Set<UUID>,
)
