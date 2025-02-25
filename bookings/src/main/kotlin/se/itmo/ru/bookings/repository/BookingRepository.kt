package se.itmo.ru.bookings.repository

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import se.itmo.ru.bookings.entity.Booking
import se.itmo.ru.common.BookingStatus
import java.time.LocalDateTime
import java.util.*

@Repository
interface BookingRepository: ReactiveCrudRepository<Booking, UUID> {

    @Query("""
        insert into booking 
        values (:bookingId, :renter, :startDate, :endDate, :status, :description)
        returning *
    """)
    fun createBooking(
        bookingId: UUID,
        renter: UUID,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        status: BookingStatus,
        description: String?
    ): Mono<Booking>

    @Query("""
        update booking
        set status = :status
        where booking_id = :bookingId
    """)
    fun updateBookingStatus(
        bookingId: UUID,
        bookingStatus: String
    ): Mono<Void>

    @Query("""
        select * from booking
        where booking_id = :bookingId::UUID
    """)
    fun getById(bookingId: UUID): Mono<Booking>
}