package se.itmo.ru.wishlists.entity

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import se.itmo.ru.wishlists.enum.WishlistStatus
import java.util.UUID

@Table(name = "wishlist_item")
data class WishlistItem(

    @Id
    @field:NotNull
    val wishlistId: UUID,

    //Account
    @field:NotNull
    val owner: UUID,

    @field:NotNull
    @field:Size(min = 1, max = 300)
    val title: String,

    val description: String? = null,

    //Item
    var foundItem: UUID? = null,

    @field:NotNull
    var status: WishlistStatus,

    val moderated: Boolean = false,
)
