package se.itmo.ru.sharedthings.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import se.itmo.ru.sharedthings.entity.Account
import se.itmo.ru.sharedthings.entity.Item
import se.itmo.ru.sharedthings.enums.WishlistStatus

data class WishlistItemDto(

    @field:NotNull(message = "wishlist id cannot be null")
    var wishlistId: Int = 0,

    var owner: Account? = null,

    @field:NotBlank(message = "title cannot be blank")
    @field:Size(max = 300, message = "title cannot be more than 300 characters.")
    val title: String,

    val description: String? = null,

    val foundItem: Item? = null,

    @field:NotNull(message = "status cannot be null")
    var status: WishlistStatus = WishlistStatus.OPEN,

    @field:NotNull(message = "moderated cannot be null")
    var moderated: Boolean = false,

    @field:NotNull(message = "suggestions cannot be null")
    var suggestions: MutableSet<Item> = mutableSetOf(),
)