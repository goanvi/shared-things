package se.itmo.ru.wishlists.rest.controller

import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.web.bind.annotation.*
import se.itmo.ru.wishlists.dto.request.MoveWishListToBookingRequest
import se.itmo.ru.wishlists.dto.request.UpdateWishlistItemRequest
import se.itmo.ru.wishlists.dto.request.WishlistItemRequest
import se.itmo.ru.wishlists.dto.response.BookingResponse
import se.itmo.ru.wishlists.dto.response.WishlistItemResponse
import se.itmo.ru.wishlists.entity.WishlistItem
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
    ): WishlistItem =
        wishlistService.createWishlistItem(wishlistItemRequest)

    @GetMapping("/{id}")
    suspend fun getWishlistById(@PathVariable("id") wishlistId: UUID): WishlistItemResponse =
        wishlistService.getWishListById(wishlistId)

    @GetMapping("/owner/{id}")
    suspend fun getModeratedWishlistItemsByOwner(
        @PathVariable("id") ownerId: UUID,
        @RequestParam("page") page: Int,
        @RequestParam("size") size: Int
    ): Page<WishlistItemResponse> =
        wishlistService.getAllModeratedWishlistByOwnerId(ownerId, PageRequest.of(page, size))

    @GetMapping("/suggestions/{id}")
    suspend fun getWishlistSuggestions(
        @PathVariable("id") wishlistId: UUID,
        @RequestParam("page") page: Int,
        @RequestParam("size") size: Int
    ): Page<UUID> =
        wishlistService.getWishlistSuggestions(wishlistId, PageRequest.of(page, size))

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

    //Admin
    @PatchMapping("/status/{id}")
    suspend fun changeWishlistStatus(
        @PathVariable("id") wishlistId: UUID,
        @RequestBody wishlistStatus: WishlistStatus
    ): WishlistItemResponse =
        wishlistService.changeStatus(wishlistId, wishlistStatus)

    @PostMapping("/moderate")
    suspend fun moderateWishlists(
        @RequestBody ids: List<UUID>
    ): Unit =
        wishlistService.moderateWishlists(ids)

    @GetMapping("/unmoderated")
    suspend fun getUnmoderatedWishlists(
        @RequestParam("page") page: Int,
        @RequestParam("size") size: Int
    ): Page<WishlistItemResponse> =
        wishlistService.getUnmoderatedWishlists(PageRequest.of(page, size))
}