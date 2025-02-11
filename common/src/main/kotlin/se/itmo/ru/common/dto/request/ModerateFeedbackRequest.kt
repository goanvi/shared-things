package se.itmo.ru.common.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotNull
import java.util.*

data class ModerateFeedbackRequest(

    @field:NotNull
    @field:JsonProperty("item_id")
    val itemId: UUID,

    @field:NotNull
    @field:JsonProperty("booking_id")
    val bookingId: UUID
)
