package se.itmo.ru.bookings.entity

import jakarta.validation.constraints.NotNull
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table(name = "booked_items")
data class BookedItems(

    @field:NotNull
    val itemId: UUID,

    @field:NotNull
    val bookingId: UUID
)
