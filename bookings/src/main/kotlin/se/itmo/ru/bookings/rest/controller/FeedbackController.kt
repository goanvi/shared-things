package se.itmo.ru.bookings.rest.controller

import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import se.itmo.ru.bookings.service.FeedbackService
import se.itmo.ru.common.dto.request.FeedbackRequest
import se.itmo.ru.common.dto.request.ModerateFeedbackRequest
import se.itmo.ru.common.dto.response.FeedbackResponse
import java.util.*

@RestController
@RequestMapping("feedback")
class FeedbackController(
    private val feedbackService: FeedbackService
) {

    @PostMapping("/create")
    fun createFeedback(@Valid @RequestBody feedbackRequest: FeedbackRequest): Mono<FeedbackResponse> =
        feedbackService.createFeedback(feedbackRequest)

    @GetMapping("/{itemId}/{bookingId}")
    fun getFeedbackByIds(
        @PathVariable("itemId") itemId: UUID,
        @PathVariable("bookingId") bookingId: UUID
    ): Mono<FeedbackResponse> =
        feedbackService.getFeedbackByIds(itemId, bookingId)

    @GetMapping("/moderated/{bookingId}")
    fun getModeratedFeedbacks(
        @PathVariable("bookingId") bookingId: UUID,
        @RequestParam(value = "page", defaultValue = "0") page: Int,
        @RequestParam(value = "size", defaultValue = "10") size: Int
    ): Mono<Page<FeedbackResponse>> =
        feedbackService.getAllModeratedFeedBackByBookingId(bookingId, PageRequest.of(page, validatePageSize(size)))

    //Admin
    @GetMapping("/unmoderated")
    @PreAuthorize("hasAuthority('ADMIN')")
    fun getUnmoderatedFeedback(
        @RequestParam(value = "page", defaultValue = "0") page: Int,
        @RequestParam(value = "size", defaultValue = "10") size: Int
    ): Mono<Page<FeedbackResponse>> =
        feedbackService.getAllUnmoderatedFeedback(PageRequest.of(page, validatePageSize(size)))

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/moderate")
    fun setFeedbackAsModerated(@RequestBody feedbackIds: Set<ModerateFeedbackRequest>): Mono<Void> =
        feedbackService.setFeedbackAsModerated(feedbackIds)

    private fun validatePageSize(size: Int) =
        if (size > 50) 50
        else size
}