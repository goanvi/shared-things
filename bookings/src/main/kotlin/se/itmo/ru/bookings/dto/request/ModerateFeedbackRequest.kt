package se.itmo.ru.bookings.dto.request

import jakarta.validation.constraints.NotNull
import java.util.*

data class ModerateFeedbackRequest(

    @field:NotNull
    val itemId: UUID,

    @field:NotNull
    val bookingId: UUID
)
