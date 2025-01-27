package se.itmo.ru.bookings.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import se.itmo.ru.bookings.dto.request.FeedbackRequest
import se.itmo.ru.bookings.dto.response.FeedbackResponse
import se.itmo.ru.bookings.entity.Feedback
import se.itmo.ru.bookings.repository.FeedbackRepository
import java.time.LocalDateTime
import java.util.*

@Service
class FeedbackService(
    private val feedbackRepository: FeedbackRepository,
) {
    fun createFeedback(feedbackRequest: FeedbackRequest): Mono<FeedbackResponse> =
        feedbackRepository.createFeedback(
            itemId = feedbackRequest.itemId,
            bookingId = feedbackRequest.bookingId,
            title = feedbackRequest.title,
            description = feedbackRequest.description,
            date = LocalDateTime.now(),
            rate = feedbackRequest.rate,
            moderated = false
        ).map { it.toResponse() }
//        if (feedbackDto.itemId != null && feedbackDto.bookingId != null) {
//            feedbackDto.item = itemProvider.getItemById(feedbackDto.itemId)
//            feedbackDto.booking = bookingProvider.getBookingById(feedbackDto.bookingId)
//        }else if (feedbackDto.item == null || feedbackDto.booking == null) {
//            throw DomainException("Cannot create feedback, item or booking is empty")
//        }
//        feedbackDto.date = LocalDateTime.now()
//        feedbackDto.moderated = false
//        return feedbackProvider.saveFeedback(feedbackDto.toEntity()).toDto()


    fun getAllUnmoderatedFeedback(pageable: Pageable): Mono<Page<FeedbackResponse>> =
        feedbackRepository.getAllUnmoderatedFeedbacks(pageable.pageSize, pageable.offset)
            .collectList()
            .map { PageImpl(it.map { feedback -> feedback.toResponse() }, pageable, it.size.toLong()) }

    fun setFeedbackAsModerated(feedbackIds: Set<Pair<UUID, UUID>>): Unit =
        feedbackRepository.moderateFeedbacks(feedbackIds)

    fun getFeedbackByIds(itemId: UUID, bookingId: UUID): Mono<FeedbackResponse> =
        feedbackRepository.getFeedbackById(itemId, bookingId).map { it.toResponse() }

    fun getAllModeratedFeedBackByBookingId(bookingId: UUID, pageable: Pageable): Mono<Page<FeedbackResponse>> =
        feedbackRepository.getAllModeratedFeedBackByBookingId(bookingId, pageable.pageSize, pageable.offset)
            .collectList()
            .map { PageImpl(it.map { feedback -> feedback.toResponse() }, pageable, it.size.toLong()) }

    private fun Feedback.toResponse(): FeedbackResponse =
        FeedbackResponse(
            itemId = itemId,
            bookingId = bookingId,
            title = title,
            description = description,
            date = date,
            rate = rate,
            moderated = moderated
        )
}