package se.itmo.ru.sharedthings.controller

import jakarta.servlet.http.HttpServletResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.*
import se.itmo.ru.sharedthings.dto.AccountDto
import se.itmo.ru.sharedthings.dto.FeedbackDto
import se.itmo.ru.sharedthings.dto.ItemDto
import se.itmo.ru.sharedthings.enums.ItemStatus
import se.itmo.ru.sharedthings.enums.WishlistStatus
import se.itmo.ru.sharedthings.service.AccountService
import se.itmo.ru.sharedthings.service.FeedbackService
import se.itmo.ru.sharedthings.service.ItemService
import se.itmo.ru.sharedthings.service.WishlistItemService

@RestController
@RequestMapping("api/admin")
class AdminController(
    private val accountService: AccountService,
    private val itemService: ItemService,
    private val feedbackService: FeedbackService,
    private val wishlistItemService: WishlistItemService
) {

    @GetMapping("/account/unmoderated")
    fun getUnmoderatedAccount(pageable: Pageable): List<AccountDto> =
        accountService.getAllUnmoderatedAccount(pageable)

    @PostMapping("/account/moderate")
    fun setAccountAsModerated(@RequestBody accountIds: Set<Int>): Int =
        accountService.setAccountsAsModerated(accountIds)

    @PostMapping("/item/moderate")
    fun setItemAsModerated(@RequestBody itemIds: Set<Int>): Int =
        itemService.setItemsAsModerated(itemIds)

    @PutMapping("/item/status/{itemId}")
    fun updateItemStatus(
        @PathVariable("itemId") itemId: Int,
        @RequestBody status: ItemStatus
    ): Unit =
        itemService.updateItemStatus(itemId, status)

    @GetMapping("/item/unmoderated")
    fun getUnmoderatedItems(pageable: Pageable, response: HttpServletResponse): Page<ItemDto> {
        val page = itemService.getAllUnmoderatedItems(pageable)
        response.addHeader("X-Total-Count", page.totalElements.toString())
        return page
    }

    @GetMapping("/feedback/unmoderated")
    fun getUnmoderatedFeedback(pageable: Pageable): Page<FeedbackDto> =
        feedbackService.getAllUnmoderatedFeedback(pageable)

    @PostMapping("/feedback/moderate")
    fun setFeedbackAsModerated(@RequestBody feedbackIds: Set<Pair<Int, Int>>): Int =
        feedbackService.setFeedbackAsModerated(feedbackIds)

    @PostMapping("/wishlist/status/{id}")
    fun changeWishlistStatus(
        @PathVariable("id") wishlistId: Int,
        @RequestBody wishlistStatus: WishlistStatus
    ): Unit =
        wishlistItemService.changeStatus(wishlistId, wishlistStatus)
}