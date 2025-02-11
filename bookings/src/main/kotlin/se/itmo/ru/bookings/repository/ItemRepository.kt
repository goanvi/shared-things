package se.itmo.ru.bookings.repository

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import se.itmo.ru.bookings.entity.Item
import se.itmo.ru.common.ItemStatus
import java.util.*

@Repository
interface ItemRepository : ReactiveCrudRepository<Item, UUID> {

    @Query("""
        insert into item
        values (:itemId, :name, :description, :owner, :status, :moderated)
        returning *
    """)
    fun createItem(
        itemId: UUID,
        name: String,
        description: String?,
        owner: UUID,
        status: ItemStatus,
        moderated: Boolean
    ):Mono<Item>

    @Query(
        """
        select * from item 
        where owner_id = :accountId and moderated = true 
        limit :limit 
        offset :offset
    """
    )
    fun getAllModeratedAccountItems(
        accountId: UUID,
        limit: Int,
        offset: Long
    ): Flux<Item>

    @Query(
        """
        select * from item 
        where moderated = false 
        limit :limit 
        offset :offset
    """
    )
    fun getAllUnmoderatedItems(
        limit: Int,
        offset: Long
    ): Flux<Item>

    @Query("""
        update item
        set name = :name, description = :description, moderated = false
        where item_id = :itemId
        returning *
    """)
    fun updateItem(
        itemId: UUID,
        name: String,
        description: String?
    ): Mono<Item>

    @Query("""
        update item
        set status = :status
        where item_id = :itemId::UUID
    """)
    fun updateItemStatus(
        itemId: UUID,
        status: String
    ): Mono<Void>

    @Query("""
        update item
        set moderated = true
        where item_id in (:itemIds)
    """)
    fun moderateItems(
        itemIds: List<UUID>
    ): Mono<Void>
}