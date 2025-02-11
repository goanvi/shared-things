package se.itmo.ru.common.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Future
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime
import java.util.*

data class BookingRequest(

    @field:NotNull
    val renter: UUID,

    @field:NotNull
    @field:Future
    @field:JsonProperty("end_date")
    val endDate: LocalDateTime,

    val description: String?,

    @field:NotNull(message = "items cannot be null")
    @field:JsonProperty("booked_items")
    val bookedItems: Set<UUID>,
)
