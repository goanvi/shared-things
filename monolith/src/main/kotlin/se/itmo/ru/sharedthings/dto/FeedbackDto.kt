package se.itmo.ru.sharedthings.dto

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import se.itmo.ru.sharedthings.entity.Booking
import se.itmo.ru.sharedthings.entity.Item
import java.time.LocalDateTime

data class FeedbackDto(

    var item: Item? = null,

    val itemId: Int? = null,

    var booking: Booking? = null,

    val bookingId: Int? = null,

    @field:NotBlank(message = "title can not be blank")
    @field:Size(max = 300, message = "title can not be longer than 300 characters")
    val title: String,

    val description: String? = null,

    @field:NotNull(message = "date can not be null")
    var date: LocalDateTime = LocalDateTime.now(),

    @field:NotNull(message = "rate can not be null")
    @field:Max(10, message = "rate can not be more than 10")
    @field:Min(1, message = "rate can not be less than 1")
    val rate: Int,

    @field:NotNull(message = "moderated can not be null")
    var moderated: Boolean = false
)
