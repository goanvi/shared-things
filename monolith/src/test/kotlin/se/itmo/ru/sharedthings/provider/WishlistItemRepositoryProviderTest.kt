package se.itmo.ru.sharedthings.provider

import jakarta.persistence.EntityExistsException
import jakarta.persistence.EntityNotFoundException
import jakarta.persistence.PersistenceException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import se.itmo.ru.sharedthings.entity.Account
import se.itmo.ru.sharedthings.entity.Item
import se.itmo.ru.sharedthings.entity.WishlistItem
import se.itmo.ru.sharedthings.enums.ItemStatus
import se.itmo.ru.sharedthings.enums.WishlistStatus
import se.itmo.ru.sharedthings.repository.AccountRepository
import se.itmo.ru.sharedthings.repository.ItemRepository
import se.itmo.ru.sharedthings.repository.WishlistItemRepository
import java.util.*

class WishlistItemRepositoryProviderTest {

    private lateinit var wishlistItemRepository: WishlistItemRepository
    private lateinit var accountRepository: AccountRepository
    private lateinit var itemRepository: ItemRepository
    private lateinit var wishlistItemRepositoryProvider: WishlistItemRepositoryProvider

    @BeforeEach
    fun setUp() {
        wishlistItemRepository = mock(WishlistItemRepository::class.java)
        accountRepository = mock(AccountRepository::class.java)
        itemRepository = mock(ItemRepository::class.java)
        wishlistItemRepositoryProvider =
            WishlistItemRepositoryProvider(wishlistItemRepository, accountRepository, itemRepository)
    }

    @Test
    fun `test saveWishlistItem with new wishlistItem`() {
        val wishlistItem = createWishlistItem()

        `when`(wishlistItemRepository.existsById(anyInt())).thenReturn(false)
        `when`(accountRepository.existsById(anyInt())).thenReturn(true)
        `when`(wishlistItemRepository.save(any(WishlistItem::class.java))).thenReturn(wishlistItem)

        val savedWishlistItem = wishlistItemRepositoryProvider.saveWishlistItem(wishlistItem)

        assertNotNull(savedWishlistItem)
        assertEquals(wishlistItem, savedWishlistItem)
        verify(wishlistItemRepository, times(1)).save(wishlistItem)
    }

    @Test
    fun `test saveWishlistItem with existing wishlistId`() {
        val wishlistItem = createWishlistItem()

        `when`(wishlistItemRepository.existsById(anyInt())).thenReturn(true)

        assertThrows(EntityExistsException::class.java) {
            wishlistItemRepositoryProvider.saveWishlistItem(wishlistItem)
        }
    }

    @Test
    fun `test saveWishlistItem with non-existing owner`() {
        val wishlistItem = createWishlistItem()

        `when`(wishlistItemRepository.existsById(anyInt())).thenReturn(false)
        `when`(accountRepository.existsById(anyInt())).thenReturn(false)

        assertThrows(PersistenceException::class.java) {
            wishlistItemRepositoryProvider.saveWishlistItem(wishlistItem)
        }
    }

    @Test
    fun `test updateWishlistItem with valid wishlistItem`() {
        val wishlistId = 1
        val wishlistItem = createWishlistItemWishFoundItem(wishlistId = wishlistId)

        `when`(wishlistItemRepository.findById(wishlistId)).thenReturn(Optional.of(wishlistItem))
        `when`(itemRepository.existsById(anyInt())).thenReturn(true)
        `when`(wishlistItemRepository.save(any(WishlistItem::class.java))).thenReturn(wishlistItem)

        val updatedWishlistItem = wishlistItemRepositoryProvider.updateWishlistItem(wishlistItem)

        assertNotNull(updatedWishlistItem)
        assertEquals(wishlistItem, updatedWishlistItem)
        verify(wishlistItemRepository, times(1)).save(wishlistItem)
    }

    @Test
    fun `test updateWishlistItem with non-existing wishlistItem`() {
        val wishlistId = 1
        val wishlistItem = createWishlistItem(wishlistId = wishlistId)

        `when`(wishlistItemRepository.findById(wishlistId)).thenReturn(Optional.empty())

        assertThrows(EntityNotFoundException::class.java) {
            wishlistItemRepositoryProvider.updateWishlistItem(wishlistItem)
        }
    }

    @Test
    fun `test updateWishlistItem with non-existing foundItem`() {
        val wishlistId = 1
        val wishlistItem = createWishlistItemWishFoundItem(wishlistId = wishlistId)

        `when`(wishlistItemRepository.findById(wishlistId)).thenReturn(Optional.of(wishlistItem))
        `when`(itemRepository.existsById(anyInt())).thenReturn(false)

        assertThrows(PersistenceException::class.java) {
            wishlistItemRepositoryProvider.updateWishlistItem(wishlistItem)
        }
    }

    @Test
    fun `test getAllModeratedWishlistItemByOwner`() {
        val owner = Account(accountId = 1, username = "testUser", email = "test@example.com")
        val pageable = PageRequest.of(0, 10)
        val wishlistItems = listOf(
            createWishlistItem(wishlistId = 1),
            createWishlistItem(wishlistId = 2)
        )
        val page = PageImpl(wishlistItems, pageable, wishlistItems.size.toLong())

        `when`(wishlistItemRepository.findAllByModeratedAndOwner(true, owner, pageable)).thenReturn(page)

        val result = wishlistItemRepositoryProvider.getAllModeratedWishlistItemByOwner(owner, pageable)

        assertNotNull(result)
        assertEquals(wishlistItems.size, result.content.size)
        assertEquals(wishlistItems, result.content)
    }

    @Test
    fun `test getByWishlistId with existing wishlistItem`() {
        val wishlistId = 1
        val wishlistItem = createWishlistItem(wishlistId = wishlistId)

        `when`(wishlistItemRepository.findById(wishlistId)).thenReturn(Optional.of(wishlistItem))

        val result = wishlistItemRepositoryProvider.getByWishlistId(wishlistId)

        assertNotNull(result)
        assertEquals(wishlistItem, result)
    }

    @Test
    fun `test getByWishlistId with non-existing wishlistItem`() {
        val wishlistId = 1

        `when`(wishlistItemRepository.findById(wishlistId)).thenReturn(Optional.empty())

        assertThrows(EntityNotFoundException::class.java) {
            wishlistItemRepositoryProvider.getByWishlistId(wishlistId)
        }
    }


    private fun createWishlistItem(wishlistId: Int = 1): WishlistItem {
        val owner = Account(accountId = 1, username = "testUser")
        return WishlistItem(
            wishlistId = wishlistId,
            owner = owner,
            title = "wishlist title",
            status = WishlistStatus.OPEN
        )
    }

    private fun createWishlistItemWishFoundItem(wishlistId: Int = 1): WishlistItem {
        val wishlistOwner = Account(accountId = 1, username = "testUser")
        val itemOwner = Account(accountId = 2, username = "testUser2")
        val foundItem = Item(itemId = 1, name = "Test Item", owner = itemOwner, status = ItemStatus.AVAILABLE)
        return WishlistItem(
            wishlistId = wishlistId,
            owner = wishlistOwner,
            title = "wishlist title",
            status = WishlistStatus.OPEN,
            foundItem = foundItem
        )
    }
}