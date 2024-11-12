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
import se.itmo.ru.sharedthings.enums.ItemStatus
import se.itmo.ru.sharedthings.repository.AccountRepository
import se.itmo.ru.sharedthings.repository.ItemRepository
import java.util.*

class ItemRepositoryProviderTest {

    private lateinit var itemRepository: ItemRepository
    private lateinit var accountRepository: AccountRepository
    private lateinit var itemRepositoryProvider: ItemRepositoryProvider

    @BeforeEach
    fun setUp() {
        itemRepository = mock(ItemRepository::class.java)
        accountRepository = mock(AccountRepository::class.java)
        itemRepositoryProvider = ItemRepositoryProvider(itemRepository, accountRepository)
    }

    @Test
    fun `test saveItem with new item`() {
        val owner = Account(accountId = 1, username = "testUser", email = "test@example.com")
        val item = Item(itemId = 0, name = "Test Item", owner = owner, status = ItemStatus.AVAILABLE)

        `when`(itemRepository.existsById(anyInt())).thenReturn(false)
        `when`(accountRepository.existsById(anyInt())).thenReturn(true)
        `when`(itemRepository.save(any(Item::class.java))).thenReturn(item)

        val savedItem = itemRepositoryProvider.saveItem(item)

        assertNotNull(savedItem)
        assertEquals(item, savedItem)
        verify(itemRepository, times(1)).save(item)
    }

    @Test
    fun `test saveItem with existing itemId`() {
        val owner = Account(accountId = 1, username = "testUser", email = "test@example.com")
        val item = Item(itemId = 1, name = "Test Item", owner = owner, status = ItemStatus.AVAILABLE)

        `when`(itemRepository.existsById(anyInt())).thenReturn(true)

        assertThrows(EntityExistsException::class.java) {
            itemRepositoryProvider.saveItem(item)
        }
    }

    @Test
    fun `test saveItem with non-existing owner`() {
        val owner = Account(accountId = 1, username = "testUser", email = "test@example.com")
        val item = Item(itemId = 0, name = "Test Item", owner = owner, status = ItemStatus.AVAILABLE)

        `when`(itemRepository.existsById(anyInt())).thenReturn(false)
        `when`(accountRepository.existsById(anyInt())).thenReturn(false)

        assertThrows(PersistenceException::class.java) {
            itemRepositoryProvider.saveItem(item)
        }
    }

    @Test
    fun `test updateItem with valid item`() {
        val itemId = 1
        val item = Item(
            itemId = itemId,
            name = "Updated Item",
            owner = Account(accountId = 1, username = "username"),
            status = ItemStatus.AVAILABLE
        )

        `when`(itemRepository.findById(itemId)).thenReturn(Optional.of(item))
        `when`(itemRepository.save(any(Item::class.java))).thenReturn(item)

        val updatedItem = itemRepositoryProvider.updateItem(item)

        assertNotNull(updatedItem)
        assertEquals(item, updatedItem)
        verify(itemRepository, times(1)).save(item)
    }

    @Test
    fun `test updateItem with non-existing item`() {
        val itemId = 1
        val item = Item(
            itemId = itemId,
            name = "Updated Item",
            owner = Account(accountId = 1, username = "username"),
            status = ItemStatus.AVAILABLE
        )

        `when`(itemRepository.findById(itemId)).thenReturn(Optional.empty())

        assertThrows(EntityNotFoundException::class.java) {
            itemRepositoryProvider.updateItem(item)
        }
    }

    @Test
    fun `test getAllModeratedAccountItems with existing account`() {
        val accountId = 1
        val owner = Account(accountId = accountId, username = "username")
        val pageable = PageRequest.of(0, 10)
        val items = listOf(
            Item(
                itemId = 1,
                name = "Item 1",
                owner = owner,
                status = ItemStatus.AVAILABLE
            ),
            Item(
                itemId = 2,
                name = "Item 2",
                owner = owner,
                status = ItemStatus.AVAILABLE
            )
        )
        val page = PageImpl(items, pageable, items.size.toLong())

        `when`(accountRepository.existsById(accountId)).thenReturn(true)
        `when`(itemRepository.findAllByModeratedAndOwnerAccountId(true, accountId, pageable)).thenReturn(page)

        val result = itemRepositoryProvider.getAllModeratedAccountItems(accountId, pageable)

        assertNotNull(result)
        assertEquals(items.size, result.content.size)
        assertEquals(items, result.content)
    }

    @Test
    fun `test getAllModeratedAccountItems with non-existing account`() {
        val accountId = 1
        val pageable = PageRequest.of(0, 10)

        `when`(accountRepository.existsById(accountId)).thenReturn(false)

        assertThrows(EntityNotFoundException::class.java) {
            itemRepositoryProvider.getAllModeratedAccountItems(accountId, pageable)
        }
    }

    @Test
    fun `test getAllUnmoderatedItem`() {
        val pageable = PageRequest.of(0, 10)
        val owner = Account(accountId = 1, username = "username")
        val items = listOf(
            Item(
                itemId = 1,
                name = "Item 1",
                owner = owner,
                status = ItemStatus.AVAILABLE
            ),
            Item(
                itemId = 2,
                name = "Item 2",
                owner = owner,
                status = ItemStatus.AVAILABLE
            )
        )
        val page = PageImpl(items, pageable, items.size.toLong())

        `when`(itemRepository.findAllByModerated(false, pageable)).thenReturn(page)

        val result = itemRepositoryProvider.getAllUnmoderatedItem(pageable)

        assertNotNull(result)
        assertEquals(items.size, result.content.size)
        assertEquals(items, result.content)
    }

    @Test
    fun `test getItemById with existing item`() {
        val itemId = 1
        val item = Item(
            itemId = itemId,
            name = "Test Item",
            owner = Account(accountId = 1, username = "username"),
            status = ItemStatus.AVAILABLE
        )

        `when`(itemRepository.findById(itemId)).thenReturn(Optional.of(item))

        val result = itemRepositoryProvider.getItemById(itemId)

        assertNotNull(result)
        assertEquals(item, result)
    }

    @Test
    fun `test getItemById with non-existing item`() {
        val itemId = 1

        `when`(itemRepository.findById(itemId)).thenReturn(Optional.empty())

        assertThrows(EntityNotFoundException::class.java) {
            itemRepositoryProvider.getItemById(itemId)
        }
    }

    @Test
    fun `test setItemAsModerated`() {
        val itemIds = setOf(1, 2, 3)

        `when`(itemRepository.setItemAsModerated(itemIds)).thenReturn(itemIds.size)

        val result = itemRepositoryProvider.setItemAsModerated(itemIds)

        assertEquals(itemIds.size, result)
        verify(itemRepository, times(1)).setItemAsModerated(itemIds)
    }

    @Test
    fun `test updateItemStatus`() {
        val itemId = 1
        val item = Item(
            itemId = itemId,
            name = "Test Item",
            owner = Account(accountId = 1, username = "username"),
            status = ItemStatus.AVAILABLE
        )
        val newStatus = ItemStatus.BOOKED

        `when`(itemRepository.findById(itemId)).thenReturn(Optional.of(item))
        `when`(itemRepository.save(any(Item::class.java))).thenReturn(item)

        itemRepositoryProvider.updateItemStatus(itemId, newStatus)

        assertEquals(newStatus, item.status)
        verify(itemRepository, times(1)).save(item)
    }
}