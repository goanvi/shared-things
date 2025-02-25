package se.itmo.ru.wishlists.repository

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import se.itmo.ru.wishlists.entity.WishlistSuggestions
import java.util.UUID

interface WishlistSuggestionsRepository: CrudRepository<WishlistSuggestions, UUID> {

    @Query("""
        insert into wishlist_suggestions
        values (:itemId, :wishlistId)
        returning *
    """)
    fun createSuggestions(
        itemId: UUID,
        wishlistId: UUID
    ): WishlistSuggestions

    @Query("""
        select item_id from wishlist_suggestions 
        where wishlist_id = :wishlistId
    """)
    fun getAllByWishlistId(
        wishlistId: UUID,
        limit: Int,
        offset: Long
    ):List<UUID>

}