package se.itmo.ru.sharedthings.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import se.itmo.ru.sharedthings.entity.Account
import se.itmo.ru.sharedthings.entity.Item
import se.itmo.ru.sharedthings.entity.WishlistItem

@Repository
interface WishlistItemRepository : JpaRepository<WishlistItem, Int> {

    fun findAllByModerated(moderated: Boolean, pageable: Pageable): Page<WishlistItem>

    fun findAllByModeratedAndOwner(moderated: Boolean, owner: Account, pageable: Pageable): Page<WishlistItem>

}

