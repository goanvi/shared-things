package se.itmo.ru.wishlists.service

import feign.FeignException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.withContext
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import se.itmo.ru.common.dto.request.BookingRequest
import se.itmo.ru.wishlists.dto.request.MoveWishListToBookingRequest
import se.itmo.ru.wishlists.dto.request.UpdateWishlistItemRequest
import se.itmo.ru.wishlists.dto.request.WishlistItemRequest
import se.itmo.ru.wishlists.dto.response.BookingResponse
import se.itmo.ru.wishlists.dto.response.WishlistItemResponse
import se.itmo.ru.wishlists.entity.WishlistItem
import se.itmo.ru.wishlists.enum.WishlistStatus
import se.itmo.ru.wishlists.exception.DomainException
import se.itmo.ru.wishlists.repository.WishlistItemRepository
import se.itmo.ru.wishlists.repository.WishlistSuggestionsRepository
import se.itmo.ru.wishlists.rest.client.AccountRestClient
import se.itmo.ru.wishlists.rest.client.BookingRestClient
import java.util.*

@Service
class WishlistItemService(
    private val wishlistItemRepository: WishlistItemRepository,
    private val wishlistSuggestionsRepository: WishlistSuggestionsRepository,
    private val accountRestClient: AccountRestClient,
    private val bookingRestClient: BookingRestClient,
) {
    @Transactional
    suspend fun createWishlistItem(wishlistItemRequest: WishlistItemRequest): WishlistItem {
        try {
            accountRestClient.getAccountById(wishlistItemRequest.owner).awaitSingle()
        } catch (ex: FeignException) {
            throw DomainException("Account with ${wishlistItemRequest.owner} id not found")
        }
        return withContext(Dispatchers.IO) {

            wishlistItemRepository.createWishlistItem(
                wishlistId = UUID.randomUUID(),
                owner = wishlistItemRequest.owner,
                title = wishlistItemRequest.title,
                description = wishlistItemRequest.description,
                foundItem = null,
                status = WishlistStatus.OPEN,
                moderated = false
            )

        }
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
        try {
            accountRestClient.getAccountById(ownerId).awaitSingle()
            return withContext(Dispatchers.IO) {
                val items =
                    wishlistItemRepository.getAllModeratedWishlistByOwner(
                        ownerId,
                        pageable.pageSize,
                        pageable.offset
                    )
                        .map {
                            it.toResponse()
                        }
                PageImpl(items, pageable, wishlistItemRepository.count())
            }
        } catch (ex: FeignException) {
            throw DomainException("Account with $ownerId id not found")
        }
    }

    @Transactional
    suspend fun addItemToWishlistSuggestions(itemId: UUID, wishlistId: UUID): Unit {
        try {
            bookingRestClient.getItemById(itemId).awaitSingle()
        } catch (ex: FeignException) {
            throw DomainException("Item with $itemId id not found")
        }
        withContext(Dispatchers.IO) {
            wishlistSuggestionsRepository.createSuggestions(
                itemId = itemId,
                wishlistId = wishlistId
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
        try {
            bookingRestClient.getItemById(moveWishListToBookingRequest.foundItemId).awaitSingle()
            val wishlistItem = getWishListById(moveWishListToBookingRequest.wishlistId)
            val response = bookingRestClient.createBooking(
                BookingRequest(
                    renter = wishlistItem.owner,
                    endDate = moveWishListToBookingRequest.endDateOfBooking,
                    description = null,
                    bookedItems = setOf(moveWishListToBookingRequest.foundItemId)
                )
            ).awaitSingle()
            return withContext(Dispatchers.IO) {

                wishlistItemRepository.addFoundItem(
                    moveWishListToBookingRequest.wishlistId,
                    moveWishListToBookingRequest.foundItemId
                )
                BookingResponse(response.bookingId)
            }
        } catch (ex: FeignException) {
            throw DomainException(ex.message ?: "Item with ${moveWishListToBookingRequest.foundItemId} id not found")
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