package se.itmo.ru.sharedthings.controller

import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.*
import se.itmo.ru.sharedthings.dto.BookingDto
import se.itmo.ru.sharedthings.dto.ItemDto
import se.itmo.ru.sharedthings.dto.WishlistItemDto
import se.itmo.ru.sharedthings.service.WishlistItemService
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("api/wishlist")
class WishlistController(
    private val wishlistService: WishlistItemService
) {

    @PostMapping("/create")
    fun createWishlistItem(
        @RequestParam("accountId") accountId: Int,
        @Valid @RequestBody wishlistItemDto: WishlistItemDto
    ): WishlistItemDto =
        wishlistService.createWishlistItem(accountId, wishlistItemDto)

    @GetMapping("/{id}")
    fun getWishlistById(@PathVariable("id") wishlistId: Int): WishlistItemDto =
        wishlistService.getWishListById(wishlistId)

    @GetMapping("/owner")
    fun getModeratedWishlistItemsByOwner(
        @RequestParam("ownerId") ownerId: Int,
        pageable: Pageable
    ): Page<WishlistItemDto> =
        wishlistService.getAllModeratedWishlistByOwnerId(ownerId, pageable)

    @PostMapping("/suggestions")
    fun addItemToWishlistSuggestions(
        @RequestParam("itemId") itemId: Int,
        @RequestParam("wishlistId") wishlistId: Int
    ): Unit =
        wishlistService.addItemToWishlistSuggestions(itemId, wishlistId)

    @PutMapping("/{id}")
    fun updateWishlist(
        @PathVariable("id") accountId: Int,
        @Valid @RequestBody wishlistItemDto: WishlistItemDto
    ) =
        wishlistService.updateWishlist(accountId, wishlistItemDto)

    @PostMapping("/book")
    fun moveWishListToBooking(
        @RequestParam("wishlistId") wishlistId: Int,
        @RequestParam("foundItemId") foundItemId: Int,
        @RequestParam("endDateOfBooking") endDateOfBooking: String
    ): BookingDto =
        LocalDateTime.parse(endDateOfBooking).let {
            wishlistService.moveWishlistToBooking(wishlistId, foundItemId, it)
        }
}