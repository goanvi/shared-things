package se.itmo.ru.bookings.dto.response

import java.time.LocalDateTime
import java.util.*

data class FeedbackResponse(

    val itemId: UUID,

    val bookingId: UUID,

    val title: String,

    val description: String?,

    var date: LocalDateTime,

    val rate: Int,

    val moderated: Boolean
)
