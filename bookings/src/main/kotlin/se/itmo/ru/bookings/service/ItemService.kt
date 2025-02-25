package se.itmo.ru.bookings.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import se.itmo.ru.common.dto.request.ItemRequest
import se.itmo.ru.common.dto.request.UpdateItemRequest
import se.itmo.ru.common.dto.response.ItemResponse
import se.itmo.ru.bookings.entity.Item
import se.itmo.ru.common.ItemStatus
import se.itmo.ru.bookings.exception.DomainException
import se.itmo.ru.bookings.repository.ItemRepository
import se.itmo.ru.bookings.rest.client.AccountRestClient
import java.util.*

@Service
class ItemService(
    private val itemRepository: ItemRepository,
    private val accountRestClient: AccountRestClient,
) {
    fun createItem(itemRequest: ItemRequest): Mono<ItemResponse> {
        val renterCheckMono = accountRestClient.getAccountById(itemRequest.owner).switchIfEmpty(
            Mono.error(DomainException("Renter with id ${itemRequest.owner} does not exist"))
        )
        return renterCheckMono
            .then(Mono.fromCallable { UUID.randomUUID() })
            .flatMap { itemId ->
                itemRepository.createItem(
                    itemId = itemId,
                    name = itemRequest.name,
                    description = itemRequest.description,
                    owner = itemRequest.owner,
                    status = ItemStatus.AVAILABLE,
                    moderated = false
                ).map { it.toResponse() }
            }
    }

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

    fun updateItemStatus(itemId: UUID, status: ItemStatus): Mono<Void> {
        return itemRepository.updateItemStatus(itemId, status.name)
    }

    fun setItemsAsModerated(itemIds: Set<UUID>): Mono<Void> =
        itemRepository.moderateItems(itemIds.toList())

    fun existsById(itemId: UUID): Mono<Boolean> =
        itemRepository.existsById(itemId)

    fun getById(itemId: UUID): Mono<ItemResponse> =
        itemRepository.findById(itemId).map { it.toResponse() }

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