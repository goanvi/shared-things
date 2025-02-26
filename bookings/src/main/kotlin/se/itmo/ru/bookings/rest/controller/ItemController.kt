package se.itmo.ru.bookings.rest.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import se.itmo.ru.bookings.service.ItemService
import se.itmo.ru.common.ItemStatus
import se.itmo.ru.common.dto.request.ItemRequest
import se.itmo.ru.common.dto.request.UpdateItemRequest
import se.itmo.ru.common.dto.response.FeedbackResponse
import se.itmo.ru.common.dto.response.ItemResponse
import java.util.*

@RestController
@RequestMapping("item")
@Tag(name = "Прдеметы")
class ItemController(
    private val service: ItemService
) {

    @Operation(
        summary = "Создание предмета",
        description = "Позволяет создать предмет"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "Предмет создан",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = ItemResponse::class)
                )]
            ),
            ApiResponse(responseCode = "400", description = "Некорректный запрос"),
            ApiResponse(responseCode = "422", description = "Некореектные данные")
        ]
    )
    @PostMapping("/create")
    fun createItem(
        @Valid @RequestBody itemRequest: ItemRequest
    ): Mono<ItemResponse> =
        service.createItem(itemRequest)

    @Operation(
        summary = "Получить все вещи по аккаунту",
        description = "Позволяет получить предметы аккаунту"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "Предметы по аккаунту получены",
            ),
            ApiResponse(responseCode = "400", description = "Некорректный запрос"),
            ApiResponse(responseCode = "422", description = "Некореектные данные")
        ]
    )
    @GetMapping("/account/{id}")
    fun getModeratedAccountItems(
        @PathVariable("id") accountId: UUID,
        @RequestParam(value = "page", defaultValue = "0") page: Int,
        @RequestParam(value = "size", defaultValue = "10") size: Int
    ): Mono<Page<ItemResponse>> =
        service.getAllModeratedAccountItems(accountId, PageRequest.of(page, validatePageSize(size)))

    @Operation(
        summary = "Обновить предмет по id",
        description = "Позволяет Обновить предмет по id"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "Предметы успешно обновлен",
            ),
            ApiResponse(responseCode = "400", description = "Некорректный запрос"),
            ApiResponse(responseCode = "422", description = "Некореектные данные")
        ]
    )
    @PutMapping("/{id}")
    fun updateAccountItem(
        @PathVariable("id") itemId: UUID,
        @Valid @RequestBody updateItemRequest: UpdateItemRequest
    ): Mono<ItemResponse> =
        service.updateItem(itemId, updateItemRequest)

    @Operation(
        summary = "Получить предмет по id",
        description = "Позволяет получить предмет по id"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "Предметы успешно получен",
                content = [Content(
                    mediaType = "application/json",
                )]
            ),
            ApiResponse(responseCode = "400", description = "Некорректный запрос"),
            ApiResponse(responseCode = "422", description = "Некореектные данные")
        ]
    )
    @GetMapping("/{id}")
    fun getItemById(
        @PathVariable("id") itemId: UUID
    ): Mono<ItemResponse> =
        service.getById(itemId)

    //Admin
    @Operation(
        summary = "Помечаем предметы как проверенные (только для админов)",
        description = "Позволяет проверить передеметы (только для админов)"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "Предметы успешно проверены",
            ),
            ApiResponse(responseCode = "400", description = "Некорректный запрос"),
            ApiResponse(responseCode = "403", description = "Нет доступа"),
            ApiResponse(responseCode = "422", description = "Некореектные данные")
        ]
    )
    @PostMapping("/moderate")
    fun setItemAsModerated(@RequestBody itemIds: Set<UUID>): Mono<Void> =
        service.setItemsAsModerated(itemIds)

    @Operation(
        summary = "Обновить статус предмета (только для админов)",
        description = "Позволяет обновить статус предмета (только для админов)"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "Статус предмета успешно обновлен",
                content = [Content(
                    mediaType = "application/json",
                )]
            ),
            ApiResponse(responseCode = "400", description = "Некорректный запрос"),
            ApiResponse(responseCode = "403", description = "Нет доступа"),
            ApiResponse(responseCode = "422", description = "Некореектные данные")
        ]
    )
    @PatchMapping("/status/{itemId}")
    fun updateItemStatus(
        @PathVariable("itemId") itemId: UUID,
        @RequestBody status: ItemStatus
    ): Mono<Void> =
        service.updateItemStatus(itemId, status)

    @Operation(
        summary = "Получение непроверенных предметов (только для админов)",
        description = "Позволяет получить непроверенные предметы (только для админов)"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "Предметы получены",
                content = [Content(
                    mediaType = "application/json",
                )]
            ),
            ApiResponse(responseCode = "400", description = "Некорректный запрос"),
            ApiResponse(responseCode = "403", description = "Нет доступа"),
            ApiResponse(responseCode = "422", description = "Некореектные данные")
        ]
    )
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