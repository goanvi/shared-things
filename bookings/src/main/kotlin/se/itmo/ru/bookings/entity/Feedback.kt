package se.itmo.ru.bookings.entity

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import java.util.*

@Table(name = "feedback")
data class Feedback(

    @field:NotNull
    @field:Column("item_id")
    val itemId: UUID,

    @field:NotNull
    @field:Column("booking_id")
    val bookingId: UUID,

    @field:Size(min = 1 ,max = 300)
    @field:NotNull
    val title: String,

    val description: String?,

    @field:NotNull
    val date: LocalDateTime,

    @field:NotNull
    @field:Max(10)
    @field:Min(1)
    val rate: Int,

    @field:NotNull
    val moderated: Boolean = false
)
