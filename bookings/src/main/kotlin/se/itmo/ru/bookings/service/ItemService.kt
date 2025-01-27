package se.itmo.ru.bookings.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import se.itmo.ru.bookings.dto.request.ItemRequest
import se.itmo.ru.bookings.dto.request.UpdateItemRequest
import se.itmo.ru.bookings.dto.response.ItemResponse
import se.itmo.ru.bookings.entity.Item
import se.itmo.ru.bookings.enum.ItemStatus
import se.itmo.ru.bookings.repository.ItemRepository
import java.util.*

@Service
class ItemService(
    private val itemRepository: ItemRepository,
) {
    fun createItem(itemRequest: ItemRequest): Mono<ItemResponse> {
        return itemRepository.createItem(
            itemId = UUID.randomUUID(),
            name = itemRequest.name,
            description = itemRequest.description,
            owner = itemRequest.owner,
            status = ItemStatus.AVAILABLE,
            moderated = false
        ).map { it.toResponse() }
    }
//        accountProvider.getAccountById(accountId)
//            .let {
//                itemDto.itemId = 0
//                itemDto.owner = it
//                itemDto.moderated = false
//                itemDto.status = ItemStatus.AVAILABLE
//                itemRepository.saveItem(itemDto.toEntity())
//            }
//            .toDto()

    fun getAllModeratedAccountItems(accountId: UUID, pageable: Pageable): Mono<Page<ItemResponse>> =
        itemRepository.getAllModeratedAccountItems(accountId, pageable.pageSize, pageable.offset)
            .collectList()
            .map {
                PageImpl(it.map { item -> item.toResponse() }, pageable, it.size.toLong())
            }

    fun getAllUnmoderatedItems(pageable: Pageable): Mono<Page<ItemResponse>> =
        itemRepository.getAllUnmoderatedItems(pageable.pageSize, pageable.offset)
            .collectList()
            .map {
                PageImpl(it.map { item -> item.toResponse() }, pageable, it.size.toLong())
            }

    fun updateItem(itemId: UUID, updateItemRequest: UpdateItemRequest): Mono<ItemResponse> =
        itemRepository.updateItem(
            itemId = itemId,
            name = updateItemRequest.name,
            description = updateItemRequest.description
        ).map { it.toResponse() }

    fun updateItemStatus(itemId: UUID, status: ItemStatus):Mono<Void> {
        return itemRepository.updateItemStatus(itemId, status.name)
    }

    fun setItemsAsModerated(itemIds: Set<UUID>): Unit =
        itemRepository.moderateItems(itemIds)

    private fun Item.toResponse(): ItemResponse =
        ItemResponse(
            itemId = itemId,
            name = name,
            description = description,
            owner = owner,
            status = status,
            moderated = moderated,
        )
}