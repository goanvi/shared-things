package se.itmo.ru.sharedthings.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import se.itmo.ru.sharedthings.entity.WishlistItem

@Repository
interface WishlistItemRepository: JpaRepository<WishlistItem, Int> {
}

