package se.itmo.ru.sharedthings.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import se.itmo.ru.sharedthings.dto.ItemDto
import se.itmo.ru.sharedthings.entity.Account
import se.itmo.ru.sharedthings.entity.Item
import se.itmo.ru.sharedthings.enums.ItemStatus
import se.itmo.ru.sharedthings.exceptions.DtoMapException
import se.itmo.ru.sharedthings.exceptions.IllegalExecutionException
import se.itmo.ru.sharedthings.provider.AccountRepositoryProvider
import se.itmo.ru.sharedthings.provider.ItemRepositoryProvider

class ItemServiceTest {

    private lateinit var itemProvider: ItemRepositoryProvider
    private lateinit var accountProvider: AccountRepositoryProvider
    private lateinit var itemService: ItemService

    @BeforeEach
    fun setUp() {
        itemProvider = mock(ItemRepositoryProvider::class.java)
        accountProvider = mock(AccountRepositoryProvider::class.java)
        itemService = ItemService(itemProvider, accountProvider)
    }

    @Test
    fun `test createItem`() {
        val accountId = 1
        val account = Account(accountId = accountId, username = "testUser", email = "test@example.com")
        val itemDto = ItemDto(itemId = 0, name = "Test Item", description = "Test Description", owner = account)
        val item = itemDto.toEntity().copy(owner = account, moderated = false, status = ItemStatus.AVAILABLE)

        `when`(accountProvider.getAccountById(accountId)).thenReturn(account)
        `when`(itemProvider.saveItem(item)).thenReturn(item)

        val createdItemDto = itemService.createItem(accountId, itemDto)

        assertNotNull(createdItemDto)
        assertEquals(itemDto.name, createdItemDto.name)
        assertEquals(itemDto.description, createdItemDto.description)
        assertEquals(account, createdItemDto.owner)
        assertEquals(ItemStatus.AVAILABLE, createdItemDto.status)
        assertFalse(createdItemDto.moderated)
        verify(itemProvider, times(1)).saveItem(item)
    }

    @Test
    fun `test getAllModeratedAccountItems`() {
        val accountId = 1
        val owner = Account(accountId = accountId, username = "testUser", email = "test@example.com")
        val pageable = PageRequest.of(0, 10)
        val items = listOf(
            Item(itemId = 1, name = "Item 1", owner = owner, status = ItemStatus.AVAILABLE),
            Item(itemId = 2, name = "Item 2", owner = owner, status = ItemStatus.AVAILABLE)
        )
        val page = PageImpl(items, pageable, items.size.toLong())

        `when`(itemProvider.getAllModeratedAccountItems(accountId, pageable)).thenReturn(page)

        val result = itemService.getAllModeratedAccountItems(accountId, pageable)

        assertNotNull(result)
        assertEquals(items.size, result.content.size)
        assertEquals(items.map { it.toDto() }, result.content)
        verify(itemProvider, times(1)).getAllModeratedAccountItems(accountId, pageable)
    }

    @Test
    fun `test getAllUnmoderatedItems`() {
        val pageable = PageRequest.of(0, 10)
        val owner = Account(accountId = 1, username = "testUser", email = "test@example.com")
        val items = listOf(
            Item(itemId = 1, name = "Item 1", owner = owner, status = ItemStatus.AVAILABLE),
            Item(itemId = 2, name = "Item 2", owner = owner, status = ItemStatus.AVAILABLE)
        )
        val page = PageImpl(items, pageable, items.size.toLong())

        `when`(itemProvider.getAllUnmoderatedItem(pageable)).thenReturn(page)

        val result = itemService.getAllUnmoderatedItems(pageable)

        assertNotNull(result)
        assertEquals(items.size, result.content.size)
        assertEquals(items.map { it.toDto() }, result.content)
        verify(itemProvider, times(1)).getAllUnmoderatedItem(pageable)
    }

    @Test
    fun `test updateAccountItem with valid item`() {
        val accountId = 1
        val itemId = 1
        val account = Account(accountId = accountId, username = "testUser", email = "test@example.com")
        val item = Item(itemId = itemId, name = "Test Item", owner = account, status = ItemStatus.AVAILABLE)
        val itemDto = ItemDto(itemId = itemId, name = "Updated Item", description = "Updated Description")

        `when`(itemProvider.getItemById(itemId)).thenReturn(item)
        `when`(itemProvider.updateItem(item)).thenReturn(item)

        itemService.updateAccountItem(accountId, itemDto)

        verify(itemProvider, times(1)).updateItem(
            itemDto.toEntity().copy(owner = account, moderated = false, status = item.status)
        )
    }

    @Test
    fun `test updateAccountItem with invalid account`() {
        val accountId = 2
        val itemId = 1
        val account = Account(accountId = 1, username = "testUser", email = "test@example.com")
        val item = Item(itemId = itemId, name = "Test Item", owner = account, status = ItemStatus.AVAILABLE)
        val itemDto = ItemDto(itemId = itemId, name = "Updated Item", description = "Updated Description")

        `when`(itemProvider.getItemById(itemId)).thenReturn(item)

        assertThrows(IllegalExecutionException::class.java) {
            itemService.updateAccountItem(accountId, itemDto)
        }
    }

    @Test
    fun `test updateItemStatus`() {
        val itemId = 1
        val status = ItemStatus.DISABLED

        itemService.updateItemStatus(itemId, status)

        verify(itemProvider, times(1)).updateItemStatus(itemId, status)
    }

    @Test
    fun `test setItemsAsModerated`() {
        val itemIds = setOf(1, 2, 3)

        `when`(itemProvider.setItemAsModerated(itemIds)).thenReturn(itemIds.size)

        val result = itemService.setItemsAsModerated(itemIds)

        assertEquals(itemIds.size, result)
        verify(itemProvider, times(1)).setItemAsModerated(itemIds)
    }

    private fun ItemDto.toEntity(): Item =
        Item(
            itemId = itemId,
            name = name,
            description = description,
            owner = owner ?: throw DtoMapException("Illegal map dto to item"),
            status = status,
            moderated = moderated,
        )

    private fun Item.toDto(): ItemDto =
        ItemDto(
            itemId = itemId,
            name = name,
            description = description,
            owner = owner,
            status = status,
            moderated = moderated,
        )
}