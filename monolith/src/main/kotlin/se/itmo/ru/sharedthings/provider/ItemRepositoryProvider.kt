package se.itmo.ru.sharedthings.provider

import jakarta.persistence.EntityExistsException
import jakarta.persistence.EntityNotFoundException
import jakarta.persistence.PersistenceException
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import se.itmo.ru.sharedthings.entity.Item
import se.itmo.ru.sharedthings.enums.ItemStatus
import se.itmo.ru.sharedthings.repository.AccountRepository
import se.itmo.ru.sharedthings.repository.ItemRepository

@Component
class ItemRepositoryProvider(
    private val itemRepository: ItemRepository,
    private val accountRepository: AccountRepository
) {
    @Transactional
    fun saveItem(item: Item): Item {
        return when {
            item.itemId != 0
                    && itemRepository.existsById(item.itemId) ->
                throw EntityExistsException("Item with id ${item.itemId} already exists")

            !accountRepository.existsById(item.owner.accountId) ->
                throw PersistenceException("Owner with id ${item.owner.accountId} does not exist")

            else -> itemRepository.save(item)
        }
    }

    @Transactional
    fun updateItem(item: Item): Item =
        itemRepository.findById(item.itemId)
            .orElseThrow { throw EntityNotFoundException("Item with id ${item.itemId} not found") }
            .let { itemRepository.save(item) }

    @Transactional
    fun getAllModeratedAccountItems(accountId: Int, pageable: Pageable): Page<Item> =
        if (accountRepository.existsById(accountId)) {
            itemRepository.findAllByModeratedAndOwnerAccountId(true, accountId, pageable)
        } else {
            throw EntityNotFoundException("Items with owner $accountId not found")
        }


    fun getAllUnmoderatedItem(pageable: Pageable): Page<Item> =
        itemRepository.findAllByModerated(false, pageable)


    fun getItemById(itemId: Int): Item =
        itemRepository.findById(itemId).orElseThrow { throw EntityNotFoundException("Item with id $itemId not found") }

    fun setItemAsModerated(itemIds: Set<Int>): Int =
        itemRepository.setItemAsModerated(itemIds)

    fun updateItemStatus(itemId: Int, status: ItemStatus) {
        getItemById(itemId).let {
            it.status = status
            itemRepository.save(it)
        }
    }
}