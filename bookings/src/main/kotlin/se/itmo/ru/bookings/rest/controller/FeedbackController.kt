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
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import se.itmo.ru.bookings.service.FeedbackService
import se.itmo.ru.common.dto.request.FeedbackRequest
import se.itmo.ru.common.dto.request.ModerateFeedbackRequest
import se.itmo.ru.common.dto.response.BookingResponse
import se.itmo.ru.common.dto.response.FeedbackResponse
import java.util.*

@RestController
@RequestMapping("feedback")
@Tag(name = "Отзывы")
class FeedbackController(
    private val feedbackService: FeedbackService
) {
    @Operation(
        summary = "Создание отзыва",
        description = "Позволяет создать отзыв"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "Отзыв создан",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = FeedbackResponse::class)
                )]
            ),
            ApiResponse(responseCode = "400", description = "Некорректный запрос"),
            ApiResponse(responseCode = "422", description = "Некореектные данные")
        ]
    )
    @PostMapping("/create")
    fun createFeedback(@Valid @RequestBody feedbackRequest: FeedbackRequest): Mono<FeedbackResponse> =
        feedbackService.createFeedback(feedbackRequest)

    @Operation(
        summary = "Получение отзыва на вещь в бронирование",
        description = "Позволяет получить отзыв на вещь в бронирование"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "Отзыв получен",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = FeedbackResponse::class)
                )]
            ),
            ApiResponse(responseCode = "400", description = "Некорректный запрос"),
            ApiResponse(responseCode = "422", description = "Некореектные данные")
        ]
    )
    @GetMapping("/{itemId}/{bookingId}")
    fun getFeedbackByIds(
        @PathVariable("itemId") itemId: UUID,
        @PathVariable("bookingId") bookingId: UUID
    ): Mono<FeedbackResponse> =
        feedbackService.getFeedbackByIds(itemId, bookingId)

    @Operation(
        summary = "Получение отзыва на вещь в бронирование",
        description = "Позволяет получить отзыв на вещь в бронирование"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "Отзыв получен",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = FeedbackResponse::class)
                )]
            ),
            ApiResponse(responseCode = "400", description = "Некорректный запрос"),
            ApiResponse(responseCode = "422", description = "Некореектные данные")
        ]
    )
    @GetMapping("/moderated/{bookingId}")
    fun getModeratedFeedbacks(
        @PathVariable("bookingId") bookingId: UUID,
        @RequestParam(value = "page", defaultValue = "0") page: Int,
        @RequestParam(value = "size", defaultValue = "10") size: Int
    ): Mono<Page<FeedbackResponse>> =
        feedbackService.getAllModeratedFeedBackByBookingId(bookingId, PageRequest.of(page, validatePageSize(size)))

    //Admin
    @Operation(
        summary = "Получение непроверенных отзывов (только для админов)",
        description = "Позволяет получить непроверенные отзывы (только для админов)"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "Отзывы получены",
                content = [Content(
                    mediaType = "application/json",
//                    schema = Schema(implementation = FeedbackResponse::class)
                )]
            ),
            ApiResponse(responseCode = "400", description = "Некорректный запрос"),
            ApiResponse(responseCode = "403", description = "Нет доступа"),
            ApiResponse(responseCode = "422", description = "Некореектные данные")
        ]
    )
    @GetMapping("/unmoderated")
    @PreAuthorize("hasAuthority('ADMIN')")
    fun getUnmoderatedFeedback(
        @RequestParam(value = "page", defaultValue = "0") page: Int,
        @RequestParam(value = "size", defaultValue = "10") size: Int
    ): Mono<Page<FeedbackResponse>> =
        feedbackService.getAllUnmoderatedFeedback(PageRequest.of(page, validatePageSize(size)))

    @Operation(
        summary = "Помечаем отзывы как проверенные (только для админов)",
        description = "Позволяет проверить отзывы (только для админов)"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "Отзывы успешно проверены",
            ),
            ApiResponse(responseCode = "400", description = "Некорректный запрос"),
            ApiResponse(responseCode = "403", description = "Нет доступа"),
            ApiResponse(responseCode = "422", description = "Некореектные данные")
        ]
    )
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/moderate")
    fun setFeedbackAsModerated(@RequestBody feedbackIds: Set<ModerateFeedbackRequest>): Mono<Void> =
        feedbackService.setFeedbackAsModerated(feedbackIds)

    private fun validatePageSize(size: Int) =
        if (size > 50) 50
        else size
}