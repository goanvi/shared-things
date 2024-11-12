package se.itmo.ru.sharedthings.provider

import jakarta.persistence.EntityExistsException
import jakarta.persistence.EntityNotFoundException
import jakarta.persistence.PersistenceException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import se.itmo.ru.sharedthings.entity.Account
import se.itmo.ru.sharedthings.entity.WishlistItem
import se.itmo.ru.sharedthings.repository.AccountRepository
import se.itmo.ru.sharedthings.repository.ItemRepository
import se.itmo.ru.sharedthings.repository.WishlistItemRepository

@Component
class WishlistItemRepositoryProvider(
    private val wishlistItemRepository: WishlistItemRepository,
    private val accountRepository: AccountRepository,
    private val itemRepository: ItemRepository
) {

    fun saveWishlistItem(wishlistItem: WishlistItem): WishlistItem =
        when {
            wishlistItem.wishlistId != 0
                    && wishlistItemRepository.existsById(wishlistItem.wishlistId) ->
                throw EntityExistsException("wishlist with id ${wishlistItem.wishlistId} already exists")

            !accountRepository.existsById(wishlistItem.owner.accountId) ->
                throw PersistenceException("wishlist owner with id ${wishlistItem.owner.accountId} does not exist")

            else -> wishlistItemRepository.save(wishlistItem)
        }

    fun updateWishlistItem(wishlistItem: WishlistItem) =
        wishlistItemRepository.findById(wishlistItem.wishlistId)
            .orElseThrow { throw EntityNotFoundException("wishlist with id ${wishlistItem.wishlistId} does not exist") }
            .let {
                when {
                    wishlistItem.foundItem != null
                            && !itemRepository.existsById(it.foundItem?.itemId ?: 0) ->
                        throw PersistenceException("FoundItem does not exist")

                    else -> wishlistItemRepository.save(wishlistItem)
                }
            }

    fun getAllModeratedWishlistItemByOwner(owner: Account, pageable: Pageable): Page<WishlistItem> =
        wishlistItemRepository.findAllByModeratedAndOwner(true, owner, pageable)

    fun getByWishlistId(wishlistId: Int): WishlistItem =
        wishlistItemRepository.findById(wishlistId)
            .orElseThrow { throw EntityNotFoundException("wishlist with id ${wishlistId} does not exist") }

}