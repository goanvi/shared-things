package se.itmo.ru.bookings.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import se.itmo.ru.bookings.entity.Item

@Repository
interface ItemRepository : JpaRepository<Item, Int> {

    fun findAllByModerated(moderated: Boolean, pageable: Pageable): Page<Item>

    @Query("select i from Item i where i.moderated = :moderated and i.owner.accountId = :accountId")
    fun findAllByModeratedAndOwnerAccountId(moderated: Boolean, accountId: Int, pageable: Pageable): Page<Item>

    @Modifying
    @Query("update Item i set i.moderated = true where i.itemId in (:itemIds)")
    fun setItemAsModerated(itemIds: Set<Int>): Int
}