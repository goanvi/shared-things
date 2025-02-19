package se.itmo.ru.wishlists.rest.controller

import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.security.access.prepost.PreAuthorize
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
@RequestMapping("wishlist")
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
        @RequestParam(value = "page", defaultValue = "0") page: Int,
        @RequestParam(value = "size", defaultValue = "10") size: Int
    ): Page<WishlistItemResponse> =
        wishlistService.getAllModeratedWishlistByOwnerId(ownerId, PageRequest.of(page, validatePageSize(size)))

    @GetMapping("/suggestions/{id}")
    suspend fun getWishlistSuggestions(
        @PathVariable("id") wishlistId: UUID,
        @RequestParam(value = "page", defaultValue = "0") page: Int,
        @RequestParam(value = "size", defaultValue = "10") size: Int
    ): Page<UUID> =
        wishlistService.getWishlistSuggestions(wishlistId, PageRequest.of(page, validatePageSize(size)))

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
    @PreAuthorize("hasRole('ADMIN')")
    suspend fun changeWishlistStatus(
        @PathVariable("id") wishlistId: UUID,
        @RequestBody wishlistStatus: WishlistStatus
    ): WishlistItemResponse =
        wishlistService.changeStatus(wishlistId, wishlistStatus)

    @PostMapping("/moderate")
    @PreAuthorize("hasRole('ADMIN')")
    suspend fun moderateWishlists(
        @RequestBody ids: List<UUID>
    ): Unit =
        wishlistService.moderateWishlists(ids)

    @GetMapping("/unmoderated")
    @PreAuthorize("hasRole('ADMIN')")
    suspend fun getUnmoderatedWishlists(
        @RequestParam(value = "page", defaultValue = "0") page: Int,
        @RequestParam(value = "size", defaultValue = "10") size: Int
    ): Page<WishlistItemResponse> =
        wishlistService.getUnmoderatedWishlists(PageRequest.of(page, validatePageSize(size)))

    private fun validatePageSize(size: Int) =
        if (size > 50) 50
        else size
}