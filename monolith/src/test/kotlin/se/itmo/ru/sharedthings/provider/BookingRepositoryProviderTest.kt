package se.itmo.ru.sharedthings.provider

import jakarta.persistence.EntityExistsException
import jakarta.persistence.EntityNotFoundException
import jakarta.persistence.PersistenceException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import se.itmo.ru.sharedthings.entity.Account
import se.itmo.ru.sharedthings.entity.Booking
import se.itmo.ru.sharedthings.enums.BookingStatus
import se.itmo.ru.sharedthings.repository.AccountRepository
import se.itmo.ru.sharedthings.repository.BookingRepository
import java.time.LocalDateTime
import java.util.*

class BookingRepositoryProviderTest {

    private lateinit var bookingRepository: BookingRepository
    private lateinit var accountRepository: AccountRepository
    private lateinit var bookingRepositoryProvider: BookingRepositoryProvider

    @BeforeEach
    fun setUp() {
        bookingRepository = mock(BookingRepository::class.java)
        accountRepository = mock(AccountRepository::class.java)
        bookingRepositoryProvider = BookingRepositoryProvider(bookingRepository, accountRepository)
    }

    @Test
    fun `test saveBooking with new booking`() {
        val renter = Account(accountId = 1, username = "testUser", email = "test@example.com")
        val booking = Booking(
            bookingId = 0,
            renter = renter,
            endDate = LocalDateTime.now(),
            status = BookingStatus.OPEN,
            bookedItems = emptySet()
        )

        `when`(bookingRepository.existsById(anyInt())).thenReturn(false)
        `when`(accountRepository.existsById(anyInt())).thenReturn(true)
        `when`(bookingRepository.save(any(Booking::class.java))).thenReturn(booking)

        val savedBooking = bookingRepositoryProvider.saveBooking(booking)

        assertNotNull(savedBooking)
        assertEquals(booking, savedBooking)
        verify(bookingRepository, times(1)).save(booking)
    }

    @Test
    fun `test saveBooking with existing bookingId`() {
        val renter = Account(accountId = 1, username = "testUser", email = "test@example.com")
        val booking = Booking(
            bookingId = 1, renter = renter,
            endDate = LocalDateTime.now(),
            status = BookingStatus.OPEN,
            bookedItems = emptySet()
        )

        `when`(bookingRepository.existsById(anyInt())).thenReturn(true)

        assertThrows(EntityExistsException::class.java) {
            bookingRepositoryProvider.saveBooking(booking)
        }
    }

    @Test
    fun `test saveBooking with non-existing renter`() {
        val renter = Account(accountId = 1, username = "testUser", email = "test@example.com")
        val booking = Booking(
            bookingId = 0, renter = renter,
            endDate = LocalDateTime.now(),
            status = BookingStatus.OPEN,
            bookedItems = emptySet()
        )

        `when`(bookingRepository.existsById(anyInt())).thenReturn(false)
        `when`(accountRepository.existsById(anyInt())).thenReturn(false)

        assertThrows(PersistenceException::class.java) {
            bookingRepositoryProvider.saveBooking(booking)
        }
    }

    @Test
    fun `test updateBooking with valid booking`() {
        val bookingId = 1
        val booking = Booking(
            bookingId = bookingId,
            renter = Account(accountId = 1, username = "testUser"),
            endDate = LocalDateTime.now(),
            status = BookingStatus.OPEN,
            bookedItems = emptySet()
        )

        `when`(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking))
        `when`(bookingRepository.save(any(Booking::class.java))).thenReturn(booking)

        val updatedBooking = bookingRepositoryProvider.updateBooking(booking)

        assertNotNull(updatedBooking)
        assertEquals(booking, updatedBooking)
        verify(bookingRepository, times(1)).save(booking)
    }

    @Test
    fun `test updateBooking with non-existing booking`() {
        val bookingId = 1
        val booking = Booking(
            bookingId = bookingId,
            renter = Account(accountId = 1, username = "testUser"),
            endDate = LocalDateTime.now(),
            status = BookingStatus.OPEN,
            bookedItems = emptySet()
        )

        `when`(bookingRepository.findById(bookingId)).thenReturn(Optional.empty())

        assertThrows(EntityNotFoundException::class.java) {
            bookingRepositoryProvider.updateBooking(booking)
        }
    }

    @Test
    fun `test getBookingById with existing booking`() {
        val bookingId = 1
        val booking = Booking(
            bookingId = bookingId,
            renter = Account(accountId = 1, username = "testUser"),
            endDate = LocalDateTime.now(),
            status = BookingStatus.OPEN,
            bookedItems = emptySet()
        )

        `when`(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking))

        val result = bookingRepositoryProvider.getBookingById(bookingId)

        assertNotNull(result)
        assertEquals(booking, result)
    }

    @Test
    fun `test getBookingById with non-existing booking`() {
        val bookingId = 1

        `when`(bookingRepository.findById(bookingId)).thenReturn(Optional.empty())

        assertThrows(EntityNotFoundException::class.java) {
            bookingRepositoryProvider.getBookingById(bookingId)
        }
    }
}