package se.itmo.ru.bookings.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import se.itmo.ru.bookings.entity.Booking
import se.itmo.ru.bookings.entity.Feedback
import se.itmo.ru.bookings.entity.Item

@Repository
interface FeedbackRepository: JpaRepository<Feedback, Int> {

    fun findAllByModerated(moderated: Boolean, pageable: Pageable): Page<Feedback>

    fun existsByItemAndBooking(item: Item, booking: Booking): Boolean

    @Modifying
    @Query("update Feedback f set f.moderated = true where (f.booking.bookingId, f.item.itemId) in (:feedbackIds) ")
    fun setFeedbacksAsModerated(feedbackIds: Set<Pair<Int,Int>>): Int

    @Query("select f from Feedback f where f.item.itemId = :itemId and f.booking.bookingId = :bookingId")
    fun findFeedbackByItemIdAndBookingId(itemId: Int, bookingId: Int): Feedback?

    @Query("select f from Feedback f where f.booking.bookingId = :bookingId and f.moderated = true")
    fun findAllModeratedFeedbackByBookingId(bookingId: Int, pageable: Pageable): Page<Feedback>
}