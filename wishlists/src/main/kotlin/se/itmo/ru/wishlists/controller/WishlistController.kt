package se.itmo.ru.wishlists.controller

import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.*
import se.itmo.ru.wishlists.dto.request.MoveWishListToBookingRequest
import se.itmo.ru.wishlists.dto.request.UpdateWishlistItemRequest
import se.itmo.ru.wishlists.dto.request.WishlistItemRequest
import se.itmo.ru.wishlists.dto.response.BookingResponse
import se.itmo.ru.wishlists.dto.response.WishlistItemResponse
import se.itmo.ru.wishlists.enum.WishlistStatus
import se.itmo.ru.wishlists.service.WishlistItemService
import java.util.*

@RestController
@RequestMapping("api/wishlist")
class WishlistController(
    private val wishlistService: WishlistItemService
) {

    @PostMapping("/create")
    suspend fun createWishlistItem(
        @Valid @RequestBody wishlistItemRequest: WishlistItemRequest
    ): UUID =
        wishlistService.createWishlistItem(wishlistItemRequest)

    @GetMapping("/{id}")
    suspend fun getWishlistById(@PathVariable("id") wishlistId: UUID): WishlistItemResponse =
        wishlistService.getWishListById(wishlistId)

    @GetMapping("/owner")
    suspend fun getModeratedWishlistItemsByOwner(
        @RequestParam("ownerId") ownerId: UUID,
        pageable: Pageable
    ): Page<WishlistItemResponse> =
        wishlistService.getAllModeratedWishlistByOwnerId(ownerId, pageable)

    @GetMapping("/suggestions")
    suspend fun getWishlistSuggestions(
        @RequestParam("wishlistId") wishlistId: UUID,
        pageable: Pageable
    ): Page<UUID> =
        wishlistService.getWishlistSuggestions(wishlistId, pageable)

    @PostMapping("/suggestions/add")
    suspend fun addItemToWishlistSuggestions(
        @RequestParam("itemId") itemId: UUID,
        @RequestParam("wishlistId") wishlistId: UUID
    ): Unit =
        wishlistService.addItemToWishlistSuggestions(itemId, wishlistId)

    @PutMapping("/{id}")
    suspend fun updateWishlist(
        @PathVariable id: UUID,
        @Valid @RequestBody wishlistItemRequest: UpdateWishlistItemRequest
    ): WishlistItemResponse =
        wishlistService.updateWishlist(id, wishlistItemRequest)

    @PostMapping("/book")
    suspend fun moveWishListToBooking(
        @Valid @RequestBody moveWishListToBookingRequest: MoveWishListToBookingRequest
    ): BookingResponse =
            wishlistService.moveWishlistToBooking(moveWishListToBookingRequest)

    @PostMapping("/admin/status/{id}")
    suspend fun changeWishlistStatus(
        @PathVariable("id") wishlistId: UUID,
        @RequestBody wishlistStatus: WishlistStatus
    ): WishlistItemResponse =
        wishlistService.changeStatus(wishlistId, wishlistStatus)

    @PostMapping("/admin/moderate")
    suspend fun moderateWishlists(
        @RequestBody ids: List<UUID>
    ): Unit =
        wishlistService.moderateWishlists(ids)

    @GetMapping("/admin/moderate")
    suspend fun getUnmoderatedWishlists(
        pageable: Pageable
    ): Page<WishlistItemResponse> =
        wishlistService.getUnmoderatedWishlists(pageable)
}