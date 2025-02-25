package se.itmo.ru.sharedthings.provider

import jakarta.persistence.EntityExistsException
import jakarta.persistence.EntityNotFoundException
import jakarta.persistence.PersistenceException
import jakarta.transaction.Transactional
import org.springframework.stereotype.Component
import se.itmo.ru.sharedthings.dto.BookingDto
import se.itmo.ru.sharedthings.entity.Booking
import se.itmo.ru.sharedthings.exceptions.DtoMapException
import se.itmo.ru.sharedthings.repository.AccountRepository
import se.itmo.ru.sharedthings.repository.BookingRepository

@Component
class BookingRepositoryProvider(
    private val bookingRepository: BookingRepository,
    private val accountRepository: AccountRepository
) {
    @Transactional
    fun saveBooking(booking: Booking): Booking {
        return when {
            booking.bookingId != 0
                    && bookingRepository.existsById(booking.bookingId) ->
                throw EntityExistsException("Booking with id '${booking.bookingId}' already exists")

            !accountRepository.existsById(booking.renter.accountId) ->
                throw PersistenceException("Renter with id '${booking.renter.accountId}' does not exist")

            else -> bookingRepository.save(booking)
        }
    }

    @Transactional
    fun updateBooking(booking: Booking) =
        bookingRepository.findById(booking.bookingId)
            .orElseThrow { throw EntityNotFoundException("Booking with id '${booking.bookingId}' does not exist") }
            .let {
                bookingRepository.save(booking)
            }


    fun getBookingById(bookingId: Int): Booking =
        bookingRepository.findById(bookingId)
            .orElseThrow { throw EntityNotFoundException("Booking with id '$bookingId' does not exist") }
}