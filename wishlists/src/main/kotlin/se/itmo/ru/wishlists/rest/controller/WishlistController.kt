package se.itmo.ru.wishlists.rest.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import se.itmo.ru.common.dto.response.FeedbackResponse
import se.itmo.ru.wishlists.dto.request.MoveWishListToBookingRequest
import se.itmo.ru.wishlists.dto.request.UpdateWishlistItemRequest
import se.itmo.ru.wishlists.dto.request.WishlistItemRequest
import se.itmo.ru.wishlists.dto.response.BookingResponse
import se.itmo.ru.wishlists.dto.response.WishlistItemResponse
import se.itmo.ru.wishlists.entity.WishlistItem
import se.itmo.ru.wishlists.enum.WishlistStatus
import se.itmo.ru.wishlists.service.WishlistItemService
import java.util.*

@RestController
@RequestMapping("wishlist")
@Tag(name = "Списки пожеланий")
class WishlistController(
    private val wishlistService: WishlistItemService
) {
    @Operation(
        summary = "Создание список пожеланий",
        description = "Позволяет создать список пожеланий"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "Список пожеланий создан",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = WishlistItem::class)
                )]
            ),
            ApiResponse(responseCode = "400", description = "Некорректный запрос"),
            ApiResponse(responseCode = "422", description = "Некореектные данные")
        ]
    )
    @PostMapping("/create")
    suspend fun createWishlistItem(
        @Valid @RequestBody wishlistItemRequest: WishlistItemRequest
    ): WishlistItem =
        wishlistService.createWishlistItem(wishlistItemRequest)

    @Operation(
        summary = "Получить список пожеланий по id",
        description = "Позволяет получить список пожеланий по его id"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "Список пожеланий получен",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = WishlistItem::class)
                )]
            ),
            ApiResponse(responseCode = "400", description = "Некорректный запрос"),
            ApiResponse(responseCode = "422", description = "Некореектные данные")
        ]
    )
    @GetMapping("/{id}")
    suspend fun getWishlistById(@PathVariable("id") wishlistId: UUID): WishlistItemResponse =
        wishlistService.getWishListById(wishlistId)

    @Operation(
        summary = "Получить список пожеланий по id владельца",
        description = "Позволяет получить список пожеланий по id владельца"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "Список пожеланий получен",
            ),
            ApiResponse(responseCode = "400", description = "Некорректный запрос"),
            ApiResponse(responseCode = "422", description = "Некореектные данные")
        ]
    )
    @GetMapping("/owner/{id}")
    suspend fun getModeratedWishlistItemsByOwner(
        @PathVariable("id") ownerId: UUID,
        @RequestParam(value = "page", defaultValue = "0") page: Int,
        @RequestParam(value = "size", defaultValue = "10") size: Int
    ): Page<WishlistItemResponse> =
        wishlistService.getAllModeratedWishlistByOwnerId(ownerId, PageRequest.of(page, validatePageSize(size)))

    @Operation(
        summary = "Получить список предложений по id списка владельца",
        description = "Позволяет получить список пожеланий по id владельца"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "Список предложений получен",
            ),
            ApiResponse(responseCode = "400", description = "Некорректный запрос"),
            ApiResponse(responseCode = "422", description = "Некореектные данные")
        ]
    )
    @GetMapping("/suggestions/{id}")
    suspend fun getWishlistSuggestions(
        @PathVariable("id") wishlistId: UUID,
        @RequestParam(value = "page", defaultValue = "0") page: Int,
        @RequestParam(value = "size", defaultValue = "10") size: Int
    ): Page<UUID> =
        wishlistService.getWishlistSuggestions(wishlistId, PageRequest.of(page, validatePageSize(size)))

    @Operation(
        summary = "Добавить в список пожеланий предложение",
        description = "Позволяет добавить в список пожеланий предложение"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "Предложение успешно добавлено",
            ),
            ApiResponse(responseCode = "400", description = "Некорректный запрос"),
            ApiResponse(responseCode = "422", description = "Некореектные данные")
        ]
    )
    @PostMapping("/suggestions/add")
    suspend fun addItemToWishlistSuggestions(
        @RequestParam("itemId") itemId: UUID,
        @RequestParam("wishlistId") wishlistId: UUID
    ): Unit =
        wishlistService.addItemToWishlistSuggestions(itemId, wishlistId)

    @Operation(
        summary = "Обновить список пожеланий",
        description = "Позволяет обновить список пожеланий"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "Список пожеланий успешно обновлен",
            ),
            ApiResponse(responseCode = "400", description = "Некорректный запрос"),
            ApiResponse(responseCode = "422", description = "Некореектные данные")
        ]
    )
    @PutMapping("/{id}")
    suspend fun updateWishlist(
        @PathVariable id: UUID,
        @Valid @RequestBody wishlistItemRequest: UpdateWishlistItemRequest
    ): WishlistItemResponse =
        wishlistService.updateWishlist(id, wishlistItemRequest)

    @Operation(
        summary = "Перевести список пожеланий в бронирование",
        description = "Позволяет перевести список пожеланий в бронирование"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "Список пожеланий успешно переведен в бронирование",
            ),
            ApiResponse(responseCode = "400", description = "Некорректный запрос"),
            ApiResponse(responseCode = "422", description = "Некореектные данные")
        ]
    )
    @PostMapping("/book")
    suspend fun moveWishListToBooking(
        @Valid @RequestBody moveWishListToBookingRequest: MoveWishListToBookingRequest
    ): BookingResponse =
            wishlistService.moveWishlistToBooking(moveWishListToBookingRequest)

    //Admin
    @Operation(
        summary = "Обновить статус списка пожеланий (только для админов)",
        description = "Позволяет обновить статус списка пожеланий (только для админов)"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "Статус списка пожеланий успешно обновлен",
            ),
            ApiResponse(responseCode = "400", description = "Некорректный запрос"),
            ApiResponse(responseCode = "403", description = "Нет доступа"),
            ApiResponse(responseCode = "422", description = "Некореектные данные")
        ]
    )
    @PatchMapping("/status/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    suspend fun changeWishlistStatus(
        @PathVariable("id") wishlistId: UUID,
        @RequestBody wishlistStatus: WishlistStatus
    ): WishlistItemResponse =
        wishlistService.changeStatus(wishlistId, wishlistStatus)

    @Operation(
        summary = "Проверить списки пожеланий (только для админов)",
        description = "Позволяет проверить списки пожеланий (только для админов)"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "Списки пожеланий успешно проверены",
            ),
            ApiResponse(responseCode = "400", description = "Некорректный запрос"),
            ApiResponse(responseCode = "403", description = "Нет доступа"),
            ApiResponse(responseCode = "422", description = "Некореектные данные")
        ]
    )
    @PostMapping("/moderate")
    @PreAuthorize("hasAuthority('ADMIN')")
    suspend fun moderateWishlists(
        @RequestBody ids: List<UUID>
    ): Unit =
        wishlistService.moderateWishlists(ids)

    @Operation(
        summary = "Получить список не проверенных списков пожеланий (только для админов)",
        description = "Позволяет получить список не проверенных списков пожеланий (только для админов)"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "Списки пожеланий успешно получены",
            ),
            ApiResponse(responseCode = "400", description = "Некорректный запрос"),
            ApiResponse(responseCode = "403", description = "Нет доступа"),
            ApiResponse(responseCode = "422", description = "Некореектные данные")
        ]
    )
    @GetMapping("/unmoderated")
    @PreAuthorize("hasAuthority('ADMIN')")
    suspend fun getUnmoderatedWishlists(
        @RequestParam(value = "page", defaultValue = "0") page: Int,
        @RequestParam(value = "size", defaultValue = "10") size: Int
    ): Page<WishlistItemResponse> =
        wishlistService.getUnmoderatedWishlists(PageRequest.of(page, validatePageSize(size)))

    private fun validatePageSize(size: Int) =
        if (size > 50) 50
        else size
}