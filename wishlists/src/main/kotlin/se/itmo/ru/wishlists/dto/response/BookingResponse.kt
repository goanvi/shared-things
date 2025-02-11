package se.itmo.ru.wishlists.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class BookingResponse(
    @field:JsonProperty("booking_id")
    val bookingId: UUID
)
