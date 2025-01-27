package se.itmo.ru.bookings.dto.request

import jakarta.validation.constraints.*
import java.util.*

data class FeedbackRequest(

    @field:NotNull
    val itemId: UUID,

    @field:NotNull
    val bookingId: UUID,

    @field:NotBlank(message = "title can not be blank")
    @field:Size(max = 300, message = "title can not be longer than 300 characters")
    val title: String,

    val description: String? = null,

    @field:NotNull(message = "rate can not be null")
    @field:Max(10, message = "rate can not be more than 10")
    @field:Min(1, message = "rate can not be less than 1")
    val rate: Int,
)
