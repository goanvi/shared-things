package se.itmo.ru.wishlists.unit.service

import feign.FeignException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import se.itmo.ru.wishlists.dto.request.MoveWishListToBookingRequest
import se.itmo.ru.wishlists.dto.request.WishlistItemRequest
import se.itmo.ru.wishlists.entity.WishlistItem
import se.itmo.ru.wishlists.enum.WishlistStatus
import se.itmo.ru.wishlists.exception.DomainException
import se.itmo.ru.wishlists.repository.WishlistItemRepository
import se.itmo.ru.wishlists.repository.WishlistSuggestionsRepository
import se.itmo.ru.wishlists.rest.client.AccountRestClient
import se.itmo.ru.wishlists.rest.client.BookingRestClient
import se.itmo.ru.wishlists.service.WishlistItemService
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@ExperimentalCoroutinesApi
@ExtendWith(MockitoExtension::class)
class WishlistItemServiceTest {

    @Mock
    private lateinit var wishlistItemRepository: WishlistItemRepository

    @Mock
    private lateinit var wishlistSuggestionsRepository: WishlistSuggestionsRepository

    @Mock
    private lateinit var accountRestClient: AccountRestClient

    @Mock
    private lateinit var bookingRestClient: BookingRestClient

    @InjectMocks
    private lateinit var wishlistItemService: WishlistItemService

    @Test
    fun `createWishlistItem should throw exception when account not found`() = runTest {
        // Given
        val request = WishlistItemRequest(
            owner = UUID.randomUUID(),
            title = "Test",
            description = "Test description"
        )
        whenever(accountRestClient.getAccountById(request.owner)).thenThrow(FeignException::class.java)

        // When & Then
        assertFailsWith<DomainException> {
            wishlistItemService.createWishlistItem(request)
        }.apply {
            assertEquals("Account with ${request.owner} id not found", message)
        }
    }

    @Test
    fun `changeStatus should update status`() = runTest {
        // Given
        val wishlistId = UUID.randomUUID()
        val newStatus = WishlistStatus.CLOSE
        val expectedItem = WishlistItem(
            wishlistId = wishlistId,
            owner = UUID.randomUUID(),
            title = "Test",
            status = newStatus,
            moderated = true
        )
        whenever(wishlistItemRepository.updateWishlistStatus(wishlistId, newStatus.name))
            .thenReturn(expectedItem)

        // When
        val result = wishlistItemService.changeStatus(wishlistId, newStatus)

        // Then
        assertEquals(expectedItem.owner, result.owner)
    }

    @Test
    fun `getWishListById should throw exception when not found`() = runTest {
        // Given
        val wishlistId = UUID.randomUUID()
        whenever(wishlistItemRepository.findById(wishlistId)).thenReturn(Optional.empty())

        // When & Then
        assertFailsWith<DomainException> {
            wishlistItemService.getWishListById(wishlistId)
        }.apply {
            assertEquals("wishlist with id $wishlistId does not exist", message)
        }
    }

    @Test
    fun `addItemToWishlistSuggestions should throw exception when item not found`() = runTest {
        // Given
        val itemId = UUID.randomUUID()
        val wishlistId = UUID.randomUUID()
        whenever(bookingRestClient.getItemById(itemId)).thenThrow(FeignException::class.java)

        // When & Then
        assertFailsWith<DomainException> {
            wishlistItemService.addItemToWishlistSuggestions(itemId, wishlistId)
        }.apply {
            assertEquals("Item with $itemId id not found", message)
        }
    }

    @Test
    fun `getUnmoderatedWishlists should return page`() = runTest {
        // Given
        val pageable: Pageable = PageRequest.of(0, 10)
        val items = listOf(
            WishlistItem(
                wishlistId = UUID.randomUUID(),
                owner = UUID.randomUUID(),
                title = "Test 1",
                status = WishlistStatus.OPEN,
                moderated = false
            )
        )
        whenever(wishlistItemRepository.getUnmoderatedWishlists(pageable.pageSize, pageable.offset))
            .thenReturn(items)
        whenever(wishlistItemRepository.count()).thenReturn(1L)

        // When
        val result = wishlistItemService.getUnmoderatedWishlists(pageable)

        // Then
        assertEquals(1, result.content.size)
        assertEquals(false, result.content[0].moderated)
    }

    @Test
    fun `moveWishlistToBooking should throw DomainException when FeignException occurs`() = runTest {
        // Given
        val request = MoveWishListToBookingRequest(
            wishlistId = UUID.randomUUID(),
            foundItemId = UUID.randomUUID(),
            endDateOfBooking = LocalDateTime.now()
        )

        whenever(bookingRestClient.getItemById(request.foundItemId)).thenThrow(FeignException::class.java)

        // When & Then
        assertFailsWith<DomainException> {
            wishlistItemService.moveWishlistToBooking(request)
        }.apply {
            assertEquals("Item with ${request.foundItemId} id not found", message)
        }

        verify(bookingRestClient).getItemById(request.foundItemId)
        verifyNoInteractions(wishlistItemRepository)
        verify(bookingRestClient, never()).createBooking(any())
    }

    @Test
    fun `getAllModeratedWishlistByOwnerId should throw DomainException when FeignException occurs`() = runTest {
        val accountId = UUID.randomUUID()
        val pageable = PageRequest.of(0, 10)

        whenever(accountRestClient.getAccountById(accountId)).thenThrow(FeignException::class.java)

        // When & Then
        assertFailsWith<DomainException> {
            wishlistItemService.getAllModeratedWishlistByOwnerId(accountId, pageable)
        }.apply {
            assertEquals("Account with ${accountId} id not found", message)
        }

        verify(accountRestClient).getAccountById(accountId)
    }
}