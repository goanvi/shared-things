package se.itmo.ru.sharedthings.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import se.itmo.ru.sharedthings.dto.BookingDto
import se.itmo.ru.sharedthings.dto.ItemDto
import se.itmo.ru.sharedthings.dto.WishlistItemDto
import se.itmo.ru.sharedthings.entity.Item
import se.itmo.ru.sharedthings.entity.WishlistItem
import se.itmo.ru.sharedthings.enums.WishlistStatus
import se.itmo.ru.sharedthings.exceptions.DtoMapException
import se.itmo.ru.sharedthings.exceptions.IllegalExecutionException
import se.itmo.ru.sharedthings.provider.AccountRepositoryProvider
import se.itmo.ru.sharedthings.provider.ItemRepositoryProvider
import se.itmo.ru.sharedthings.provider.WishlistItemRepositoryProvider
import java.time.LocalDateTime

@Service
class WishlistItemService(
    private val wishlistItemProvider: WishlistItemRepositoryProvider,
    private val accountProvider: AccountRepositoryProvider,
    private val bookingService: BookingService,
    private val itemProvider: ItemRepositoryProvider,
) {
    fun createWishlistItem(accountId: Int, wishlistItemDto: WishlistItemDto): WishlistItemDto =
        accountProvider.getAccountById(accountId).let {
            wishlistItemDto.wishlistId = 0
            wishlistItemDto.owner = it
            wishlistItemDto.status = WishlistStatus.OPEN
            wishlistItemDto.moderated = false
            wishlistItemDto.suggestions = mutableSetOf()
            wishlistItemProvider.saveWishlistItem(wishlistItemDto.toEntity())
        }.toDto()

    fun changeStatus(wishlistId: Int, wishlistStatus: WishlistStatus): Unit =
        wishlistItemProvider.getByWishlistId(wishlistId).let {
            it.status = wishlistStatus
            wishlistItemProvider.updateWishlistItem(it)
        }

    fun updateWishlist(accountId: Int, wishlistItemDto: WishlistItemDto): Unit =
        wishlistItemProvider.getByWishlistId(accountId).let {
            when {
                wishlistItemDto.status != WishlistStatus.OPEN ->
                    throw IllegalExecutionException("Cannot update wishlist item, expected status OPEN")

                it.owner.accountId != wishlistItemDto.owner?.accountId ->
                    throw IllegalExecutionException("Cannot update wishlist item, account with id $accountId doesn't own wishlist item with id ${wishlistItemDto.wishlistId} ")

                else -> {
                    wishlistItemDto.owner = it.owner
                    wishlistItemDto.moderated = false
                    wishlistItemProvider.updateWishlistItem(wishlistItemDto.toEntity())
                }
            }

        }

    fun getWishListById(wishlistId: Int): WishlistItemDto =
        wishlistItemProvider.getByWishlistId(wishlistId).toDto()

    fun getAllModeratedWishlistByOwnerId(ownerId: Int, pageable: Pageable): Page<WishlistItemDto> =
        accountProvider.getAccountById(ownerId).let {
            wishlistItemProvider.getAllModeratedWishlistItemByOwner(it, pageable).map { it.toDto() }
        }

    @Transactional
    fun addItemToWishlistSuggestions(itemId: Int, wishlistId: Int): Unit {
        wishlistItemProvider.getByWishlistId(wishlistId).let { wishlistItem ->
            itemProvider.getItemById(itemId).let { item ->
                wishlistItem.suggestions.add(item)
                wishlistItemProvider.updateWishlistItem(wishlistItem)
            }
        }
    }

    @Transactional
    fun moveWishlistToBooking(wishlistId: Int, foundItemId: Int, endDateOfBooking: LocalDateTime): BookingDto =
        wishlistItemProvider.getByWishlistId(wishlistId).let { wishlistItem ->
            itemProvider.getItemById(foundItemId).let { item ->
                wishlistItem.foundItem = item
                wishlistItem.status = WishlistStatus.BOOKED
                wishlistItemProvider.updateWishlistItem(wishlistItem)
                val bookingDto = BookingDto(
                    renter = wishlistItem.owner,
                    endDate = endDateOfBooking,
                    bookedItems = setOf(item)
                )
                bookingService.createBooking(wishlistItem.owner.accountId, bookingDto)
            }
        }


    private fun WishlistItemDto.toEntity(): WishlistItem =
        WishlistItem(
            wishlistId = wishlistId,
            owner = owner ?: throw DtoMapException("Owner required in wishlistItemDto to map to entity"),
            title = title,
            description = description,
            foundItem = foundItem,
            status = status,
            moderated = moderated,
            suggestions = suggestions,
        )

    private fun WishlistItem.toDto(): WishlistItemDto =
        WishlistItemDto(
            wishlistId = wishlistId,
            owner = owner,
            title = title,
            description = description,
            foundItem = foundItem,
            status = status,
            moderated = moderated,
            suggestions = suggestions,
        )
}