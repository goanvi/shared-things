package se.itmo.ru.bookings.rest.controller

import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import se.itmo.ru.common.dto.request.ItemRequest
import se.itmo.ru.common.dto.request.UpdateItemRequest
import se.itmo.ru.common.dto.response.ItemResponse
import se.itmo.ru.common.ItemStatus
import se.itmo.ru.bookings.service.ItemService
import java.util.*

@RestController
@RequestMapping("item")
class ItemController(
    private val service: ItemService
) {

    @PostMapping("/create")
    fun createItem(
        @Valid @RequestBody itemRequest: ItemRequest
    ): Mono<ItemResponse> =
        service.createItem(itemRequest)

    @GetMapping("/account/{id}")
    fun getModeratedAccountItems(
        @PathVariable("id") accountId: UUID,
        @RequestParam(value = "page", defaultValue = "0") page: Int,
        @RequestParam(value = "size", defaultValue = "10") size: Int
    ): Mono<Page<ItemResponse>> =
        service.getAllModeratedAccountItems(accountId, PageRequest.of(page, validatePageSize(size)))

    @PutMapping("/{id}")
    fun updateAccountItem(
        @PathVariable("id") itemId: UUID,
        @Valid @RequestBody updateItemRequest: UpdateItemRequest
    ): Mono<ItemResponse> =
        service.updateItem(itemId, updateItemRequest)

    @GetMapping("/{id}")
    fun getItemById(
        @PathVariable("id") itemId: UUID
    ): Mono<ItemResponse> =
        service.getById(itemId)

    //Admin
    @PostMapping("/moderate")
    fun setItemAsModerated(@RequestBody itemIds: Set<UUID>): Mono<Void> =
        service.setItemsAsModerated(itemIds)

    @PatchMapping("/status/{itemId}")
    fun updateItemStatus(
        @PathVariable("itemId") itemId: UUID,
        @RequestBody status: ItemStatus
    ): Mono<Void> =
        service.updateItemStatus(itemId, status)

    @GetMapping("/unmoderated")
    fun getUnmoderatedItems(
        @RequestParam(value = "page", defaultValue = "0") page: Int,
        @RequestParam(value = "size", defaultValue = "10") size: Int,
        response: ServerHttpResponse
    ): Mono<Page<ItemResponse>> {
        return service.getAllUnmoderatedItems(PageRequest.of(page, validatePageSize(size)))
            .doOnSuccess { response.headers.add("X-Total-Count", it.totalElements.toString()) }
    }

    private fun validatePageSize(size: Int) =
        if (size > 50) 50
        else size
}