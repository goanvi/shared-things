package se.itmo.ru.wishlists.entity

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table("wishlist_suggestions")
data class WishlistSuggestions(

    @field:NotNull
    @field:Min(1)
    val itemId: UUID,

    @field:NotNull
    @field:Min(1)
    val wishlistId: UUID
)
