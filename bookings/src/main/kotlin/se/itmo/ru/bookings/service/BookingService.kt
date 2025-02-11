package se.itmo.ru.bookings.service

import feign.FeignException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import se.itmo.ru.common.dto.request.BookingRequest
import se.itmo.ru.common.dto.response.BookingResponse
import se.itmo.ru.bookings.entity.Booking
import se.itmo.ru.common.BookingStatus
import se.itmo.ru.common.ItemStatus
import se.itmo.ru.bookings.exception.DomainException
import se.itmo.ru.bookings.repository.BookedItemsRepository
import se.itmo.ru.bookings.repository.BookingRepository
import se.itmo.ru.bookings.rest.client.AccountRestClient
import java.time.LocalDateTime
import java.util.*

@Service
class BookingService(
    private val bookingRepository: BookingRepository,
    private val bookedItemsRepository: BookedItemsRepository,
    private val itemsService: ItemService,
    private val accountRestClient: AccountRestClient
) {

    @Transactional
    fun createBooking(bookingRequest: BookingRequest): Mono<BookingResponse> {
        if (bookingRequest.bookedItems.isEmpty()) {
            return Mono.error(DomainException("Booked items can not be empty"))
        }
        val renterCheckMono = accountRestClient.getAccountById(bookingRequest.renter).switchIfEmpty(
            Mono.error(DomainException("Renter with id ${bookingRequest.renter} does not exist"))
        )
        val itemCheckMonos = bookingRequest.bookedItems.map { itemId ->
            itemsService.existsById(itemId)
                .flatMap { exists ->
                    if (!exists) {
                        Mono.error(DomainException("Item with id $itemId does not exist"))
                    } else {
                        Mono.just(itemId)
                    }
                }
        }
        return renterCheckMono
            .then(Mono.zip(itemCheckMonos) { r -> r })
            .then(Mono.fromCallable { UUID.randomUUID() })
            .flatMap { bookingId ->
                bookingRepository.createBooking(
                    bookingId = bookingId,
                    renter = bookingRequest.renter,
                    startDate = LocalDateTime.now(),
                    endDate = bookingRequest.endDate,
                    status = BookingStatus.OPEN,
                    description = bookingRequest.description
                )
                    .flatMap { booking ->
                        val addItemsMonos = bookingRequest.bookedItems.map { itemId ->
                            bookedItemsRepository.addItemToBooking(itemId, bookingId)
                                .then(itemsService.updateItemStatus(itemId, ItemStatus.BOOKED))
                        }
                        Flux.concat(addItemsMonos)
                            .then(Mono.just(booking.toResponse(bookingRequest.bookedItems)))
                    }
            }
    }


    @Transactional
    fun closeBooking(bookingId: UUID): Mono<Void> {
        return bookingRepository.updateBookingStatus(
            bookingId = bookingId,
            bookingStatus = BookingStatus.CLOSE.name
        )
            .thenMany(bookedItemsRepository.getBookedItems(bookingId))
            .flatMap { itemId ->
                itemsService.updateItemStatus(itemId, ItemStatus.AVAILABLE)
            }
            .then()
    }


    @Transactional
    fun getBookingById(bookingId: UUID): Mono<BookingResponse> {
        return bookedItemsRepository.getBookedItems(bookingId)
            .collectList()
            .flatMap { bookedItems ->
                bookingRepository.getById(bookingId).map {
                    it.toResponse(bookedItems.toSet())
                }
            }
    }


    private fun Booking.toResponse(
        bookedItems: Set<UUID>
    ): BookingResponse =
        BookingResponse(
            bookingId = bookingId,
            renter = renter,
            startDate = startDate,
            endDate = endDate,
            status = status,
            description = description,
            bookedItems = bookedItems
        )
}