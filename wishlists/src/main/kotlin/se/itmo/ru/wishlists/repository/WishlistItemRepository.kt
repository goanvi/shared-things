package se.itmo.ru.wishlists.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import se.itmo.ru.wishlists.entity.WishlistItem
import se.itmo.ru.wishlists.enum.WishlistStatus
import java.util.UUID

interface WishlistItemRepository : CrudRepository<WishlistItem, UUID> {

    @Query("""
        insert into wishlist_item 
        values (:wishlistId, :owner, :title, :description, :foundItem, :status, :moderated)
        returning *
    """)
    fun createWishlistItem(
        wishlistId: UUID,
        owner: UUID,
        title: String,
        description: String?,
        foundItem: UUID?,
        status: WishlistStatus,
        moderated: Boolean
    ): WishlistItem

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
        where wishlist_owner = :owner
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
        where wishlist_id in (:ids)
        returning *
    """)
    fun moderateWishlist(
        ids: List<UUID>
    ):List<WishlistItem>


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
}

