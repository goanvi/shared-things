package se.itmo.ru.bookings.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import se.itmo.ru.bookings.dto.request.BookingRequest
import se.itmo.ru.bookings.dto.response.BookingResponse
import se.itmo.ru.bookings.entity.Booking
import se.itmo.ru.bookings.enum.BookingStatus
import se.itmo.ru.bookings.enum.ItemStatus
import se.itmo.ru.bookings.repository.BookedItemsRepository
import se.itmo.ru.bookings.repository.BookingRepository
import java.time.LocalDateTime
import java.util.*

@Service
class BookingService(
    private val bookingRepository: BookingRepository,
    private val bookedItemsRepository: BookedItemsRepository,
    private val itemsService: ItemService
) {

    @Transactional
    fun createBooking(bookingRequest: BookingRequest): Mono<BookingResponse> {
        val bookingId = UUID.randomUUID()
        val response = bookingRepository.createBooking(
            bookingId = bookingId,
            renter = bookingRequest.renter,
            startDate = LocalDateTime.now(),
            endDate = bookingRequest.endDate,
            status = BookingStatus.OPEN,
            description = bookingRequest.description
        ).map { it.toResponse(bookingRequest.bookedItems) }
        bookingRequest.bookedItems.forEach {
            bookedItemsRepository.addItemToBooking(it, bookingId)
            itemsService.updateItemStatus(it, ItemStatus.BOOKED)
        }
        return response

//        return accountProvider.getAccountById(renterId)
//            .let {
//                if (bookingDto.bookedItems.isEmpty())
//                    if (bookingDto.bookedItemsIds.isEmpty())
//                        throw DomainException("Booked items can not be empty")
//                    else
//                        bookingDto.bookedItems = bookingDto.bookedItemsIds.map { itemId ->
//                            itemProvider.updateItemStatus(itemId, ItemStatus.BOOKED)
//                            itemProvider.getItemById(itemId)
//                        }.toSet()
//                else
//                    bookingDto.bookedItems.forEach { item -> itemProvider.updateItemStatus(item.itemId, ItemStatus.BOOKED) }
//                bookingDto.bookingId = 0
//                bookingDto.renter = it
//                bookingDto.startDate = LocalDateTime.now()
//                bookingDto.status = BookingStatus.OPEN
//                bookingProvider.saveBooking(bookingDto.toEntity())
//            }
//            .toDto()
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
//        bookingProvider.getBookingById(bookingId).let {
//            it.bookedItems.forEach { item -> itemProvider.updateItemStatus(item.itemId, ItemStatus.AVAILABLE) }
//            it.status = BookingStatus.CLOSE
//            bookingProvider.updateBooking(it)
//        }.toDto()


    @Transactional
    fun getBookingById(bookingId: UUID): Mono<BookingResponse> {
        return bookedItemsRepository.getBookedItems(bookingId)
            .collectList()
            .flatMap { bookedItems ->
                bookingRepository.findById(bookingId).map {
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