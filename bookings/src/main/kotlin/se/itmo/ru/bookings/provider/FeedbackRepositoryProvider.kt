package se.itmo.ru.bookings.provider

import jakarta.persistence.EntityExistsException
import jakarta.persistence.EntityNotFoundException
import jakarta.persistence.PersistenceException
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import se.itmo.ru.bookings.entity.Feedback
import se.itmo.ru.bookings.repository.BookingRepository
import se.itmo.ru.bookings.repository.FeedbackRepository
import se.itmo.ru.bookings.repository.ItemRepository

@Component
class FeedbackRepositoryProvider(
    private val feedbackRepository: FeedbackRepository,
    private val itemRepository: ItemRepository,
    private val bookingRepository: BookingRepository
) {
    @Transactional
    fun saveFeedback(feedback: Feedback): Feedback {
        return when {
            !itemRepository.existsById(feedback.item.itemId) ->
                throw PersistenceException("Item with id '${feedback.item.itemId}' does not exist")

            !bookingRepository.existsById(feedback.booking.bookingId) ->
                throw PersistenceException("Booking with id '${feedback.booking.bookingId}' does not exist")

            feedbackRepository.existsByItemAndBooking(feedback.item, feedback.booking) ->
                throw EntityExistsException("Feedback already exists")

            else -> feedbackRepository.save(feedback)
        }
    }

    fun getAllUnmoderatedFeedback(pageable: Pageable): Page<Feedback> =
        feedbackRepository.findAllByModerated(false, pageable)

    fun setFeedbacksAsModerated(feedbackIds: Set<Pair<Int, Int>>): Int =
        feedbackRepository.setFeedbacksAsModerated(feedbackIds)

    fun getFeedbackByIds(itemId: Int, bookingId: Int): Feedback =
        feedbackRepository.findFeedbackByItemIdAndBookingId(itemId, bookingId)
            ?: throw EntityNotFoundException("Item with itemId: $itemId and bookingId: $bookingId does not exist")

    fun getAllModeratedFeedBackByBookingId(bookingId: Int, pageable: Pageable): Page<Feedback> =
        feedbackRepository.findAllModeratedFeedbackByBookingId(bookingId, pageable)
}