package se.itmo.ru.bookings.dto.response

import se.itmo.ru.bookings.enum.ItemStatus
import java.util.*

data class ItemResponse(
    val itemId: UUID,

    val name: String,

    val description: String?,

    //Account
    val owner: UUID,

    val status: ItemStatus,

    val moderated: Boolean
)
