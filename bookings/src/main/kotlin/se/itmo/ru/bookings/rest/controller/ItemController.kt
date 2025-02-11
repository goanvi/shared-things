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
@RequestMapping("api/item")
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
        @RequestParam("page") page: Int,
        @RequestParam("size") size: Int
    ): Mono<Page<ItemResponse>> =
        service.getAllModeratedAccountItems(accountId, PageRequest.of(page, size))

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
        @RequestParam("page") page: Int,
        @RequestParam("size") size: Int,
        response: ServerHttpResponse
    ): Mono<Page<ItemResponse>> {
        return service.getAllUnmoderatedItems(PageRequest.of(page, size))
            .doOnSuccess { response.headers.add("X-Total-Count", it.totalElements.toString()) }
    }
}