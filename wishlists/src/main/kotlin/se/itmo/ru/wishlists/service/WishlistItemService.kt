package se.itmo.ru.wishlists.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import se.itmo.ru.wishlists.dto.request.MoveWishListToBookingRequest
import se.itmo.ru.wishlists.dto.request.UpdateWishlistItemRequest
import se.itmo.ru.wishlists.dto.request.WishlistItemRequest
import se.itmo.ru.wishlists.dto.response.BookingResponse
import se.itmo.ru.wishlists.dto.response.WishlistItemResponse
import se.itmo.ru.wishlists.entity.WishlistItem
import se.itmo.ru.wishlists.entity.WishlistSuggestions
import se.itmo.ru.wishlists.enum.WishlistStatus
import se.itmo.ru.wishlists.exception.DomainException
import se.itmo.ru.wishlists.repository.WishlistItemRepository
import se.itmo.ru.wishlists.repository.WishlistSuggestionsRepository
import java.util.*

//TODO обработать исключения написать тесты
@Service
class WishlistItemService(
    private val wishlistItemRepository: WishlistItemRepository,
    private val wishlistSuggestionsRepository: WishlistSuggestionsRepository
) {
    suspend fun createWishlistItem(wishlistItemRequest: WishlistItemRequest): UUID =
        withContext(Dispatchers.IO) {
            wishlistItemRepository.save(
                WishlistItem(
                    wishlistId = UUID.randomUUID(),
                    owner = wishlistItemRequest.owner,
                    title = wishlistItemRequest.title,
                    description = wishlistItemRequest.description,
                    foundItem = null,
                    status = WishlistStatus.OPEN,
                    moderated = false
                )
            ).wishlistId
        }

    suspend fun changeStatus(wishlistId: UUID, wishlistStatus: WishlistStatus): WishlistItemResponse =
        withContext(Dispatchers.IO) {
            wishlistItemRepository.updateWishlistStatus(wishlistId, wishlistStatus.name).toResponse()
        }

    suspend fun updateWishlist(id: UUID, updateWishlistItemRequest: UpdateWishlistItemRequest): WishlistItemResponse =
        withContext(Dispatchers.IO) {
            wishlistItemRepository.updateWishlistItem(
                wishlistId = id,
                title = updateWishlistItemRequest.title,
                description = updateWishlistItemRequest.description
            ).toResponse()
        }

    suspend fun getWishListById(wishlistId: UUID): WishlistItemResponse =
        withContext(Dispatchers.IO) {
            wishlistItemRepository.findById(wishlistId)
                .orElseThrow { throw DomainException("wishlist with id $wishlistId does not exist") }
                .toResponse()
        }

    suspend fun getAllModeratedWishlistByOwnerId(ownerId: UUID, pageable: Pageable): Page<WishlistItemResponse> {
        return withContext(Dispatchers.IO) {
            val items =
                wishlistItemRepository.getAllModeratedWishlistByOwner(ownerId, pageable.pageSize, pageable.offset)
                    .map {
                        it.toResponse()
                    }
            PageImpl(items, pageable, wishlistItemRepository.count())
        }
    }

    suspend fun addItemToWishlistSuggestions(itemId: UUID, wishlistId: UUID): Unit {
        withContext(Dispatchers.IO) {
            wishlistSuggestionsRepository.save(
                WishlistSuggestions(
                    itemId = itemId,
                    wishlistId = wishlistId
                )
            )
        }
    }

    suspend fun getWishlistSuggestions(wishlistId: UUID, pageable: Pageable): Page<UUID> {
        return withContext(Dispatchers.IO) {
            val items = wishlistSuggestionsRepository.getAllByWishlistId(wishlistId, pageable.pageSize, pageable.offset)
            PageImpl(items, pageable, wishlistSuggestionsRepository.count())
        }
    }

    @Transactional
    suspend fun moveWishlistToBooking(moveWishListToBookingRequest: MoveWishListToBookingRequest): BookingResponse {
        return withContext(Dispatchers.IO) {
            val wishlistItem = getWishListById(moveWishListToBookingRequest.wishlistId)
            //TODO: отправка создания бронирования в другой сервис
            wishlistItemRepository.addFoundItem(
                moveWishListToBookingRequest.wishlistId,
                moveWishListToBookingRequest.foundItemId
            )
            BookingResponse(0)
        }
    }

    suspend fun moderateWishlists(ids: List<UUID>): Unit {
        withContext(Dispatchers.IO) {
            wishlistItemRepository.moderateWishlist(ids)
        }
    }

    suspend fun getUnmoderatedWishlists(pageable: Pageable): Page<WishlistItemResponse> {
        return withContext(Dispatchers.IO) {
            val items = wishlistItemRepository.getUnmoderatedWishlists(pageable.pageSize, pageable.offset)
                .map { it.toResponse() }
            PageImpl(items, pageable, wishlistItemRepository.count())
        }
    }

    private fun WishlistItem.toResponse(): WishlistItemResponse =
        WishlistItemResponse(
            wishlistId = wishlistId,
            owner = owner,
            title = title,
            description = description,
            foundItem = foundItem,
            status = status,
            moderated = moderated
        )
}