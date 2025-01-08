package se.itmo.ru.bookings.controller

import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.*
import se.itmo.ru.bookings.dto.FeedbackDto
import se.itmo.ru.bookings.service.FeedbackService

@RestController
@RequestMapping("api/feedback")
class FeedbackController(
    private val feedbackService: FeedbackService
) {

    @PostMapping("/create")
    fun createFeedback(@Valid @RequestBody feedbackDto: FeedbackDto): FeedbackDto =
        feedbackService.createFeedback(feedbackDto)

    @GetMapping("/{itemId}/{bookingId}")
    fun getFeedbackByIds(
        @PathVariable("itemId") itemId: Int,
        @PathVariable("bookingId") bookingId: Int
    ): FeedbackDto =
        feedbackService.getFeedbackByIds(itemId, bookingId)

    @GetMapping("/{bookingId}")
    fun getModeratedFeedbacks(@PathVariable("bookingId") bookingId: Int, pageable: Pageable): Page<FeedbackDto> =
        feedbackService.getAllModeratedFeedBackByBookingId(bookingId, pageable)
}