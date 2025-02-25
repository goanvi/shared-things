package se.itmo.ru.wishlists.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import se.itmo.ru.wishlists.enum.WishlistStatus
import java.util.*

data class WishlistItemResponse(
    @field:JsonProperty("wishlist_id")
    val wishlistId: UUID,
    var owner: UUID,
    val title: String,
    val description: String?,
    @field:JsonProperty("found_item_id")
    val foundItem: UUID?,
    var status: WishlistStatus,
    var moderated: Boolean
)