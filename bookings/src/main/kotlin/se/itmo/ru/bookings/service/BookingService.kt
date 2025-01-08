package se.itmo.ru.bookings.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import se.itmo.ru.bookings.dto.BookingDto
import se.itmo.ru.bookings.entity.Booking
import se.itmo.ru.bookings.enum.BookingStatus
import se.itmo.ru.bookings.enum.ItemStatus
import se.itmo.ru.bookings.exception.DomainException
import se.itmo.ru.bookings.exception.DtoMapException
import se.itmo.ru.bookings.provider.AccountRepositoryProvider
import se.itmo.ru.bookings.provider.BookingRepositoryProvider
import se.itmo.ru.bookings.provider.ItemRepositoryProvider
import java.time.LocalDateTime

@Service
class BookingService(
    private val bookingProvider: BookingRepositoryProvider,
    private val accountProvider: AccountRepositoryProvider,
    private val itemProvider: ItemRepositoryProvider,
) {

    @Transactional
    fun createBooking(renterId: Int, bookingDto: BookingDto): BookingDto {
        return accountProvider.getAccountById(renterId)
            .let {
                if (bookingDto.bookedItems.isEmpty())
                    if (bookingDto.bookedItemsIds.isEmpty())
                        throw DomainException("Booked items can not be empty")
                    else
                        bookingDto.bookedItems = bookingDto.bookedItemsIds.map { itemId ->
                            itemProvider.updateItemStatus(itemId, ItemStatus.BOOKED)
                            itemProvider.getItemById(itemId)
                        }.toSet()
                else
                    bookingDto.bookedItems.forEach { item -> itemProvider.updateItemStatus(item.itemId, ItemStatus.BOOKED) }
                bookingDto.bookingId = 0
                bookingDto.renter = it
                bookingDto.startDate = LocalDateTime.now()
                bookingDto.status = BookingStatus.OPEN
                bookingProvider.saveBooking(bookingDto.toEntity())
            }
            .toDto()
    }

    @Transactional
    fun closeBooking(bookingId: Int): BookingDto =
        bookingProvider.getBookingById(bookingId).let {
            it.bookedItems.forEach { item -> itemProvider.updateItemStatus(item.itemId, ItemStatus.AVAILABLE) }
            it.status = BookingStatus.CLOSE
            bookingProvider.updateBooking(it)
        }.toDto()


    fun getBookingById(bookingId: Int): BookingDto =
        bookingProvider.getBookingById(bookingId).toDto()

    private fun Booking.toDto(): BookingDto =
        BookingDto(
            bookingId = bookingId,
            renter = renter,
            startDate = startDate,
            endDate = endDate,
            status = status,
            description = description,
            bookedItems = bookedItems
        )

    private fun BookingDto.toEntity(): Booking =
        Booking(
            bookingId = bookingId,
            renter = renter ?: throw DtoMapException("Renter required in booking to map to entity"),
            startDate = startDate,
            endDate = endDate,
            status = status,
            description = description,
            bookedItems = bookedItems

        )

}