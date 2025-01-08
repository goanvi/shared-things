package se.itmo.ru.bookings.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import se.itmo.ru.bookings.dto.FeedbackDto
import se.itmo.ru.bookings.entity.Feedback
import se.itmo.ru.bookings.exception.DomainException
import se.itmo.ru.bookings.exception.DtoMapException
import se.itmo.ru.bookings.provider.BookingRepositoryProvider
import se.itmo.ru.bookings.provider.FeedbackRepositoryProvider
import se.itmo.ru.bookings.provider.ItemRepositoryProvider
import java.time.LocalDateTime

@Service
class FeedbackService(
    private val feedbackProvider: FeedbackRepositoryProvider,
    private val itemProvider: ItemRepositoryProvider,
    private val bookingProvider: BookingRepositoryProvider
) {
    fun createFeedback(feedbackDto: FeedbackDto): FeedbackDto {
        if (feedbackDto.itemId != null && feedbackDto.bookingId != null) {
            feedbackDto.item = itemProvider.getItemById(feedbackDto.itemId)
            feedbackDto.booking = bookingProvider.getBookingById(feedbackDto.bookingId)
        }else if (feedbackDto.item == null || feedbackDto.booking == null) {
            throw DomainException("Cannot create feedback, item or booking is empty")
        }
        feedbackDto.date = LocalDateTime.now()
        feedbackDto.moderated = false
        return feedbackProvider.saveFeedback(feedbackDto.toEntity()).toDto()
    }

    fun getAllUnmoderatedFeedback(pageable: Pageable): Page<FeedbackDto> =
        feedbackProvider.getAllUnmoderatedFeedback(pageable).map { it.toDto() }

    fun setFeedbackAsModerated(feedbackIds: Set<Pair<Int,Int>>): Int =
        feedbackProvider.setFeedbacksAsModerated(feedbackIds)

    fun getFeedbackByIds(itemId: Int, bookingId: Int): FeedbackDto =
        feedbackProvider.getFeedbackByIds(itemId, bookingId).toDto()

    fun getAllModeratedFeedBackByBookingId(bookingId: Int, pageable: Pageable): Page<FeedbackDto> =
        feedbackProvider.getAllModeratedFeedBackByBookingId(bookingId, pageable).map { it.toDto() }


    private fun FeedbackDto.toEntity(): Feedback =
        Feedback(
            item = item ?: throw DtoMapException("Item required in feedbackDto to map to entity"),
            booking = booking ?: throw DtoMapException("Booking required in feedback to map to entity"),
            title = title,
            description = description,
            date = date,
            rate = rate,
            moderated = moderated
        )
    private fun Feedback.toDto(): FeedbackDto =
        FeedbackDto(
            item = item,
            itemId = item.itemId,
            booking = booking,
            bookingId = booking.bookingId,
            title = title,
            description = description,
            date = date,
            rate = rate,
            moderated = moderated
        )
}