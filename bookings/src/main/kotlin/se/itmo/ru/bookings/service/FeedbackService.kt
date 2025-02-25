package se.itmo.ru.bookings.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import se.itmo.ru.bookings.entity.Feedback
import se.itmo.ru.bookings.exception.DomainException
import se.itmo.ru.bookings.producer.KafkaProducerService
import se.itmo.ru.bookings.repository.FeedbackRepository
import se.itmo.ru.common.dto.notification.FeedbackCreatedNotificationDto
import se.itmo.ru.common.dto.request.FeedbackRequest
import se.itmo.ru.common.dto.request.ModerateFeedbackRequest
import se.itmo.ru.common.dto.response.FeedbackResponse
import java.time.LocalDateTime
import java.util.*

@Service
class FeedbackService(
    private val feedbackRepository: FeedbackRepository,
    private val bookingService: BookingService,
    private val itemService: ItemService,
    private val kafkaProducerService: KafkaProducerService,
    @Value("\${app.kafka.topics.feedback-created}")
    private val feedbackCreatedTopic: String,
) {
    fun createFeedback(feedbackRequest: FeedbackRequest): Mono<FeedbackResponse> {
        val bookingIdCheckMock = bookingService.getBookingById(feedbackRequest.bookingId).switchIfEmpty(
            Mono.error(DomainException("Booking with id ${feedbackRequest.bookingId} does not exist"))
        )
        val itemIdCheckMock = itemService.existsById(feedbackRequest.itemId).switchIfEmpty(
            Mono.error(DomainException("Item with id ${feedbackRequest.itemId} does not exist"))
        )
        return bookingIdCheckMock
            .then(itemIdCheckMock)
            .flatMap {
                val notification = FeedbackCreatedNotificationDto(
                    itemId = feedbackRequest.itemId,
                    title = feedbackRequest.title,
                    description = feedbackRequest.description,
                    rate = feedbackRequest.rate
                )
                kafkaProducerService.sendMessage(feedbackCreatedTopic, notification)
                feedbackRepository.createFeedback(
                    itemId = feedbackRequest.itemId,
                    bookingId = feedbackRequest.bookingId,
                    title = feedbackRequest.title,
                    description = feedbackRequest.description,
                    date = LocalDateTime.now(),
                    rate = feedbackRequest.rate,
                    moderated = false
                ).map { it.toResponse() }
            }
    }

    fun getAllUnmoderatedFeedback(pageable: Pageable): Mono<Page<FeedbackResponse>> =
        feedbackRepository.getAllUnmoderatedFeedbacks(pageable.pageSize, pageable.offset)
            .collectList()
            .map { PageImpl(it.map { feedback -> feedback.toResponse() }, pageable, it.size.toLong()) }

    fun setFeedbackAsModerated(feedbackIds: Set<ModerateFeedbackRequest>): Mono<Void> =
        Flux.fromIterable(feedbackIds)
            .flatMap { feedback ->
                feedbackRepository.moderateFeedback(feedback.itemId, feedback.bookingId)
            }
            .then()

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