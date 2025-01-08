package se.itmo.ru.bookings.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import se.itmo.ru.bookings.dto.ItemDto
import se.itmo.ru.bookings.entity.Item
import se.itmo.ru.bookings.enum.ItemStatus
import se.itmo.ru.bookings.exception.DtoMapException
import se.itmo.ru.bookings.exception.IllegalExecutionException
import se.itmo.ru.bookings.provider.AccountRepositoryProvider
import se.itmo.ru.bookings.provider.ItemRepositoryProvider

@Service
class ItemService(
    private val itemProvider: ItemRepositoryProvider,
    private val accountProvider: AccountRepositoryProvider,
) {
    fun createItem(accountId: Int, itemDto: ItemDto): ItemDto =
        accountProvider.getAccountById(accountId)
            .let {
                itemDto.itemId = 0
                itemDto.owner = it
                itemDto.moderated = false
                itemDto.status = ItemStatus.AVAILABLE
                itemProvider.saveItem(itemDto.toEntity())
            }
            .toDto()

    fun getAllModeratedAccountItems(accountId: Int, pageable: Pageable): Page<ItemDto> =
        itemProvider.getAllModeratedAccountItems(accountId, pageable).map { it.toDto() }

    fun getAllUnmoderatedItems(pageable: Pageable): Page<ItemDto> =
        itemProvider.getAllUnmoderatedItem(pageable).map { it.toDto() }

    fun updateAccountItem(accountId: Int, itemDto: ItemDto) {
        itemProvider.getItemById(itemDto.itemId).let {
            when {
                it.owner.accountId != accountId ->
                    throw IllegalExecutionException("Cannot update item, account with id $accountId doesn't own item with id ${itemDto.itemId} ")

                else -> {
                    itemDto.owner = it.owner
                    itemDto.moderated = false
                    itemDto.status = it.status
                    itemProvider.updateItem(itemDto.toEntity())
                }
            }

        }
    }

    fun updateItemStatus(itemId: Int, status: ItemStatus) {
        itemProvider.updateItemStatus(itemId, status)
    }

    fun setItemsAsModerated(itemIds: Set<Int>): Int =
        itemProvider.setItemAsModerated(itemIds)


    private fun ItemDto.toEntity(): Item =
        Item(
            itemId = itemId,
            name = name,
            description = description,
            owner = owner ?: throw DtoMapException("Owner required in itemDto to map to entity"),
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