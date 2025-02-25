package se.itmo.ru.bookings.repository


import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import se.itmo.ru.bookings.entity.Feedback
import java.time.LocalDateTime
import java.util.*

@Repository
interface FeedbackRepository : ReactiveCrudRepository<Feedback, UUID> {

    @Query("""
        insert into feedback 
        values (:itemId, :bookingId, :title, :description, :date, :rate, :moderated)
        returning *
    """)
    fun createFeedback(
        itemId: UUID,
        bookingId: UUID,
        title: String,
        description: String?,
        date: LocalDateTime,
        rate: Int,
        moderated: Boolean
    ): Mono<Feedback>

    @Query("""
        select * from feedback
        where item_id = :itemId and booking_id = :bookingId
    """)
    fun getFeedbackById(
        itemId: UUID,
        bookingId: UUID
    ): Mono<Feedback>

    @Query(
        """
        select * from feedback
        where moderated = false
        limit :limit
        offset :offset
    """
    )
    fun getAllUnmoderatedFeedbacks(
        limit: Int,
        offset: Long
    ): Flux<Feedback>

    @Query("""
        update feedback
        set moderated = true
        where item_id = :itemId and booking_id = :bookingId
    """)
    fun moderateFeedback(
        itemId: UUID,
        bookingId: UUID
    ): Mono<Void>

    @Query("""
        select * from feedback
        where booking_id = :bookingId and moderated = true 
        limit :limit
        offset :offset
    """)
    fun getAllModeratedFeedBackByBookingId(
        bookingId: UUID,
        limit: Int,
        offset: Long
    ): Flux<Feedback>

//    fun findAllByModerated(moderated: Boolean, pageable: Pageable): Page<Feedback>
//
//    fun existsByItemAndBooking(item: Item, booking: Booking): Boolean
//
//    @Modifying
//    @Query("update Feedback f set f.moderated = true where (f.booking.bookingId, f.item.itemId) in (:feedbackIds) ")
//    fun setFeedbacksAsModerated(feedbackIds: Set<Pair<Int,Int>>): Int
//
//    @Query("select f from Feedback f where f.item.itemId = :itemId and f.booking.bookingId = :bookingId")
//    fun findFeedbackByItemIdAndBookingId(itemId: Int, bookingId: Int): Feedback?
//
//    @Query("select f from Feedback f where f.booking.bookingId = :bookingId and f.moderated = true")
//    fun findAllModeratedFeedbackByBookingId(bookingId: Int, pageable: Pageable): Page<Feedback>
}