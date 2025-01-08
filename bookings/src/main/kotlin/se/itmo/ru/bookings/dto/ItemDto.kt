package se.itmo.ru.bookings.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import se.itmo.ru.bookings.entity.Account
import se.itmo.ru.bookings.enum.ItemStatus

data class ItemDto(
    @field:NotNull(message = "item id cannot be null")
    var itemId: Int = 0,

    @field:NotBlank(message = "name cannot be blank")
    @field:Size(max = 100, message = "name cannot be longer than 100 characters")
    val name: String,

    @field:Size(min = 1, max = 1000, message = "description cannot be longer than 1000 characters")
    val description: String? = null,

    var owner: Account? = null,

    @field:NotNull(message = "status cannot be null")
    var status: ItemStatus = ItemStatus.AVAILABLE,

    @field:NotNull(message = "moderated cannot be null")
    var moderated: Boolean = false
)
