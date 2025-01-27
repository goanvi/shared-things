package se.itmo.ru.bookings.repository

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import se.itmo.ru.bookings.entity.BookedItems
import java.util.*

interface BookedItemsRepository: ReactiveCrudRepository<BookedItems, UUID> {

    @Query("""
        insert into booked_items
        values (:itemId, :bookingId)
    """)
    fun addItemToBooking(
        itemId: UUID,
        bookingId: UUID
    ): Mono<Void>

    @Query("""
        select item_id from booked_items
        where booking_id = :bookingId
    """)
    fun getBookedItems(
        bookingId: UUID
    ): Flux<UUID>
}