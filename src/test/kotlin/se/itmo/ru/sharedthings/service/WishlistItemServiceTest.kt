package se.itmo.ru.sharedthings.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import se.itmo.ru.sharedthings.dto.BookingDto
import se.itmo.ru.sharedthings.dto.WishlistItemDto
import se.itmo.ru.sharedthings.entity.Account
import se.itmo.ru.sharedthings.entity.Item
import se.itmo.ru.sharedthings.entity.WishlistItem
import se.itmo.ru.sharedthings.enums.ItemStatus
import se.itmo.ru.sharedthings.enums.WishlistStatus
import se.itmo.ru.sharedthings.exceptions.DtoMapException
import se.itmo.ru.sharedthings.exceptions.IllegalExecutionException
import se.itmo.ru.sharedthings.provider.AccountRepositoryProvider
import se.itmo.ru.sharedthings.provider.ItemRepositoryProvider
import se.itmo.ru.sharedthings.provider.WishlistItemRepositoryProvider
import java.time.LocalDateTime

class WishlistItemServiceTest {

    private lateinit var wishlistItemProvider: WishlistItemRepositoryProvider
    private lateinit var accountProvider: AccountRepositoryProvider
    private lateinit var bookingService: BookingService
    private lateinit var itemProvider: ItemRepositoryProvider
    private lateinit var wishlistItemService: WishlistItemService

    @BeforeEach
    fun setUp() {
        wishlistItemProvider = mock(WishlistItemRepositoryProvider::class.java)
        accountProvider = mock(AccountRepositoryProvider::class.java)
        bookingService = mock(BookingService::class.java)
        itemProvider = mock(ItemRepositoryProvider::class.java)
        wishlistItemService = WishlistItemService(wishlistItemProvider, accountProvider, bookingService, itemProvider)
    }

    @Test
    fun `test createWishlistItem`() {
        val accountId = 1
        val account = Account(accountId = accountId, username = "testUser", email = "test@example.com")
        val wishlistItemDto = WishlistItemDto(wishlistId = 0, title = "Test Wishlist", owner = account)
        val wishlistItem = wishlistItemDto.toEntity()
            .copy(owner = account, status = WishlistStatus.OPEN, moderated = false, suggestions = mutableSetOf())

        `when`(accountProvider.getAccountById(accountId)).thenReturn(account)
        `when`(wishlistItemProvider.saveWishlistItem(any(WishlistItem::class.java))).thenReturn(wishlistItem)

        val createdWishlistItemDto = wishlistItemService.createWishlistItem(accountId, wishlistItemDto)

        assertNotNull(createdWishlistItemDto)
        assertEquals(wishlistItemDto.title, createdWishlistItemDto.title)
        assertEquals(account, createdWishlistItemDto.owner)
        assertEquals(WishlistStatus.OPEN, createdWishlistItemDto.status)
        assertFalse(createdWishlistItemDto.moderated)
        verify(wishlistItemProvider, times(1)).saveWishlistItem(wishlistItem)
    }

    @Test
    fun `test changeStatus`() {
        val wishlistId = 1
        val wishlistItem =
            WishlistItem(
                wishlistId = wishlistId,
                owner = Account(accountId = 1, username = "name"),
                title = "title",
                status = WishlistStatus.OPEN
            )

        `when`(wishlistItemProvider.getByWishlistId(wishlistId)).thenReturn(wishlistItem)

        wishlistItemService.changeStatus(wishlistId, WishlistStatus.CLOSE)

        assertEquals(WishlistStatus.CLOSE, wishlistItem.status)
        verify(wishlistItemProvider, times(1)).updateWishlistItem(wishlistItem)
    }

    @Test
    fun `test updateWishlist with valid wishlist`() {
        val accountId = 1
        val wishlistId = 1
        val account = Account(accountId = accountId, username = "testUser", email = "test@example.com")
        val wishlistItem =
            WishlistItem(wishlistId = wishlistId, owner = account, title = "title", status = WishlistStatus.OPEN)
        val wishlistItemDto =
            WishlistItemDto(
                wishlistId = wishlistId,
                title = "Updated Wishlist",
                status = WishlistStatus.OPEN,
                owner = account
            )

        `when`(wishlistItemProvider.getByWishlistId(wishlistId)).thenReturn(wishlistItem)

        wishlistItemService.updateWishlist(accountId, wishlistItemDto)

        verify(wishlistItemProvider, times(1)).updateWishlistItem(
            wishlistItemDto.toEntity().copy(owner = account, moderated = false)
        )
    }

    @Test
    fun `test updateWishlist with invalid status`() {
        val accountId = 1
        val wishlistId = 1
        val wishlistItemDto =
            WishlistItemDto(wishlistId = wishlistId, title = "Updated Wishlist", status = WishlistStatus.CLOSE)

        assertThrows(IllegalExecutionException::class.java) {
            wishlistItemService.updateWishlist(accountId, wishlistItemDto)
        }
    }

    @Test
    fun `test updateWishlist with invalid owner`() {
        val accountId = 2
        val wishlistId = 1
        val account = Account(accountId = 1, username = "testUser", email = "test@example.com")
        val wishlistItem =
            WishlistItem(wishlistId = wishlistId, owner = account, title = "title", status = WishlistStatus.OPEN)
        val wishlistItemDto =
            WishlistItemDto(wishlistId = wishlistId, title = "Updated Wishlist", status = WishlistStatus.OPEN)

        `when`(wishlistItemProvider.getByWishlistId(wishlistId)).thenReturn(wishlistItem)

        assertThrows(IllegalExecutionException::class.java) {
            wishlistItemService.updateWishlist(accountId, wishlistItemDto)
        }
    }

    @Test
    fun `test getWishListById`() {
        val wishlistId = 1
        val wishlistItem = WishlistItem(
            wishlistId = wishlistId,
            owner = Account(accountId = 1, username = "testUser"),
            title = "title",
            status = WishlistStatus.OPEN
        )

        `when`(wishlistItemProvider.getByWishlistId(wishlistId)).thenReturn(wishlistItem)

        val result = wishlistItemService.getWishListById(wishlistId)

        assertNotNull(result)
        assertEquals(wishlistItem.toDto(), result)
        verify(wishlistItemProvider, times(1)).getByWishlistId(wishlistId)
    }

    @Test
    fun `test getAllModeratedWishlistByOwnerId`() {
        val ownerId = 1
        val pageable = PageRequest.of(0, 10)
        val account = Account(accountId = ownerId, username = "testUser", email = "test@example.com")
        val wishlistItems = listOf(
            WishlistItem(wishlistId = 1, owner = account, title = "title", status = WishlistStatus.OPEN),
            WishlistItem(wishlistId = 2, owner = account, title = "title", status = WishlistStatus.OPEN),
        )
        val page = PageImpl(wishlistItems, pageable, wishlistItems.size.toLong())

        `when`(accountProvider.getAccountById(ownerId)).thenReturn(account)
        `when`(wishlistItemProvider.getAllModeratedWishlistItemByOwner(account, pageable)).thenReturn(page)

        val result = wishlistItemService.getAllModeratedWishlistByOwnerId(ownerId, pageable)

        assertNotNull(result)
        assertEquals(wishlistItems.size, result.content.size)
        assertEquals(wishlistItems.map { it.toDto() }, result.content)
        verify(wishlistItemProvider, times(1)).getAllModeratedWishlistItemByOwner(account, pageable)
    }

    @Test
    fun `test addItemToWishlistSuggestions`() {
        val itemId = 1
        val wishlistId = 1
        val item = Item(
            itemId = itemId,
            name = "Test Item",
            owner = Account(accountId = 1, username = "testUser"),
            status = ItemStatus.AVAILABLE
        )
        val wishlistItem =
            WishlistItem(
                wishlistId = wishlistId,
                owner = Account(accountId = 2, username = "user"),
                title = "title",
                suggestions = mutableSetOf(),
                status = WishlistStatus.OPEN
            )

        `when`(wishlistItemProvider.getByWishlistId(wishlistId)).thenReturn(wishlistItem)
        `when`(itemProvider.getItemById(itemId)).thenReturn(item)

        wishlistItemService.addItemToWishlistSuggestions(itemId, wishlistId)

        assertTrue(wishlistItem.suggestions.contains(item))
        verify(wishlistItemProvider, times(1)).updateWishlistItem(wishlistItem)
    }

    @Test
    fun `test moveWishlistToBooking`() {
        val wishlistId = 1
        val foundItemId = 1
        val endDateOfBooking = LocalDateTime.now()
        val account = Account(accountId = 1, username = "testUser", email = "test@example.com")
        val item = Item(itemId = foundItemId, name = "Test Item", owner = account, status = ItemStatus.AVAILABLE)
        val wishlistItem = WishlistItem(
            wishlistId = wishlistId,
            owner = account,
            title = "title",
            status = WishlistStatus.OPEN,
            foundItem = item
        )
        val bookingDto = BookingDto(renter = wishlistItem.owner, endDate = endDateOfBooking, bookedItems = setOf(item))

        `when`(wishlistItemProvider.getByWishlistId(wishlistId)).thenReturn(wishlistItem)
        `when`(itemProvider.getItemById(foundItemId)).thenReturn(item)
        `when`(bookingService.createBooking(bookingDto = any(BookingDto::class.java), renterId = anyInt())).thenReturn(bookingDto)

        wishlistItemService.moveWishlistToBooking(wishlistId, foundItemId, endDateOfBooking)

        assertEquals(WishlistStatus.BOOKED, wishlistItem.status)
        assertEquals(item, wishlistItem.foundItem)
        verify(wishlistItemProvider, times(1)).updateWishlistItem(wishlistItem)
        verify(bookingService, times(1)).createBooking(anyInt(), any(BookingDto::class.java))
    }

    private fun WishlistItemDto.toEntity(): WishlistItem =
        WishlistItem(
            wishlistId = wishlistId,
            owner = owner ?: throw DtoMapException("Illegal map dto to wishlist item"),
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

    private inline fun <reified T> any(type: Class<T>): T = Mockito.any(type)
}