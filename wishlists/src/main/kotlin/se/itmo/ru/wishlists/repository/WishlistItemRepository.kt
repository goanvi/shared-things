package se.itmo.ru.wishlists.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import se.itmo.ru.wishlists.entity.WishlistItem
import java.util.UUID

interface WishlistItemRepository : CrudRepository<WishlistItem, UUID> {

    @Query(
        """
            update wishlist_item
            set status = :status
            where wishlist_id = :wishlistId
            returning *
        """
    )
    fun updateWishlistStatus(
        wishlistId: UUID,
        status: String
    ): WishlistItem


    @Query(
        """
            update wishlist_item
            set title = :title, description = :description, moderated = false
            where wishlist_id = :wishlistId
            returning *
        """
    )
    fun updateWishlistItem(
        wishlistId: UUID,
        title: String,
        description: String?,
    ): WishlistItem


    @Query("""
        select * from wishlist_item
        where wishlist_owner = : owner
        limit :limit
        offset :offset
    """)
    fun getAllModeratedWishlistByOwner(
        owner: UUID,
        limit: Int,
        offset: Long
    ): List<WishlistItem>


    @Query("""
        update wishlist_item
        set found_item = :itemId, status = 'BOOKED'
        where wishlist_id = :wishlistId
        returning *
    """)
    fun addFoundItem(
        wishlistId: UUID,
        itemId: UUID
    ): WishlistItem


    @Query("""
        update wishlist_item
        set moderated = true
        where wishlist_id in :ids
    """)
    fun moderateWishlist(
        ids: List<UUID>
    ):Unit


    @Query("""
        select * from wishlist_item 
        where moderated = false
        limit :limit
        offset :offset
    """)
    fun getUnmoderatedWishlists(
        limit: Int,
        offset: Long
    ):List<WishlistItem>

    fun findAllByModerated(moderated: Boolean, pageable: Pageable): Page<WishlistItem>

    fun findAllByModeratedAndOwner(moderated: Boolean, owner: UUID, pageable: Pageable): Page<WishlistItem>

}

