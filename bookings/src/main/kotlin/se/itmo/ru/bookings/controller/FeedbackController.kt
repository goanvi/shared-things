package se.itmo.ru.bookings.controller

import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import se.itmo.ru.bookings.dto.request.FeedbackRequest
import se.itmo.ru.bookings.dto.request.ModerateFeedbackRequest
import se.itmo.ru.bookings.dto.response.FeedbackResponse
import se.itmo.ru.bookings.service.FeedbackService
import java.util.*

@RestController
@RequestMapping("api/feedback")
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
        @RequestParam("page") page: Int,
        @RequestParam("size") size: Int
    ): Mono<Page<FeedbackResponse>> =
        feedbackService.getAllModeratedFeedBackByBookingId(bookingId, PageRequest.of(page, size))

    //Admin
    @GetMapping("/unmoderated")
    fun getUnmoderatedFeedback(
        @RequestParam("page") page: Int,
        @RequestParam("size") size: Int
    ): Mono<Page<FeedbackResponse>> =
        feedbackService.getAllUnmoderatedFeedback(PageRequest.of(page, size))

    @PostMapping("/moderate")
    fun setFeedbackAsModerated(@RequestBody feedbackIds: Set<ModerateFeedbackRequest>): Mono<Void> =
        feedbackService.setFeedbackAsModerated(feedbackIds)
}