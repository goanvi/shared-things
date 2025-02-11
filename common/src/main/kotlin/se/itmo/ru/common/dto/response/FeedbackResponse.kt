package se.itmo.ru.common.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime
import java.util.*

data class FeedbackResponse(

    @field:JsonProperty("item_id")
    val itemId: UUID,

    @field:JsonProperty("booking_id")
    val bookingId: UUID,

    val title: String,

    val description: String?,

    var date: LocalDateTime,

    val rate: Int,

    val moderated: Boolean
)
