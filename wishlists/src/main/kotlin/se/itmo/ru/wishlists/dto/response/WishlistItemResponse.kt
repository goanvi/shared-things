package se.itmo.ru.wishlists.dto.response

import se.itmo.ru.wishlists.enum.WishlistStatus
import java.util.*

data class WishlistItemResponse(
    val wishlistId: UUID,
    var owner: UUID,
    val title: String,
    val description: String?,
    val foundItem: UUID?,
    var status: WishlistStatus,
    var moderated: Boolean
)