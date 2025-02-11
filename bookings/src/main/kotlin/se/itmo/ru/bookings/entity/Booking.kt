package se.itmo.ru.bookings.entity

import jakarta.validation.constraints.NotNull
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import se.itmo.ru.common.BookingStatus
import java.time.LocalDateTime
import java.util.*

@Table(name = "booking")
data class Booking(

    @field:Id
    @field:NotNull
    @field:Column("booking_id")
    val bookingId: UUID,

    //Account
    @field:NotNull
    @field:Column("renter_id")
    val renter: UUID,

    @field:NotNull
    val startDate: LocalDateTime,

    @field:NotNull
    val endDate: LocalDateTime,

    @field:NotNull
    val status: BookingStatus,

    val description: String?,
)
