package se.itmo.ru.sharedthings.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.*
import se.itmo.ru.sharedthings.dto.BookingDto
import se.itmo.ru.sharedthings.entity.Account
import se.itmo.ru.sharedthings.entity.Booking
import se.itmo.ru.sharedthings.entity.Item
import se.itmo.ru.sharedthings.enums.BookingStatus
import se.itmo.ru.sharedthings.enums.ItemStatus
import se.itmo.ru.sharedthings.exceptions.DomainException
import se.itmo.ru.sharedthings.exceptions.DtoMapException
import se.itmo.ru.sharedthings.provider.AccountRepositoryProvider
import se.itmo.ru.sharedthings.provider.BookingRepositoryProvider
import se.itmo.ru.sharedthings.provider.ItemRepositoryProvider
import java.time.LocalDateTime

class BookingServiceTest {

    private lateinit var bookingProvider: BookingRepositoryProvider
    private lateinit var accountProvider: AccountRepositoryProvider
    private lateinit var itemProvider: ItemRepositoryProvider
    private lateinit var bookingService: BookingService

    @BeforeEach
    fun setUp() {
        bookingProvider = mock(BookingRepositoryProvider::class.java)
        accountProvider = mock(AccountRepositoryProvider::class.java)
        itemProvider = mock(ItemRepositoryProvider::class.java)
        bookingService = BookingService(bookingProvider, accountProvider, itemProvider)
    }

    @Test
    fun `test createBooking with bookedItemsIds`() {
        val renterId = 1
        val account = Account(accountId = renterId, username = "testUser", email = "test@example.com")
        val itemId1 = 1
        val itemId2 = 2
        val item1 = Item(itemId = itemId1, name = "Item 1", owner = account, status = ItemStatus.BOOKED)
        val item2 = Item(itemId = itemId2, name = "Item 2", owner = account, status = ItemStatus.BOOKED)
        val bookingDto = BookingDto(
            bookingId = 0,
            bookedItemsIds = setOf(itemId1, itemId2),
            endDate = LocalDateTime.now(),
            renter = account
        )
        val booking =
            bookingDto.toEntity()
                .copy(startDate = LocalDateTime.now(), status = BookingStatus.OPEN, bookedItems = setOf(item1, item2))

        `when`(accountProvider.getAccountById(renterId)).thenReturn(account)
        `when`(itemProvider.getItemById(itemId1)).thenReturn(item1)
        `when`(itemProvider.getItemById(itemId2)).thenReturn(item2)
        `when`(bookingProvider.saveBooking(any(Booking::class.java))).thenReturn(booking)

        val createdBookingDto = bookingService.createBooking(renterId, bookingDto)

        assertNotNull(createdBookingDto)
        assertEquals(bookingDto.bookedItemsIds.size, createdBookingDto.bookedItems.size)
        assertEquals(BookingStatus.OPEN, createdBookingDto.status)
        verify(bookingProvider, times(1)).saveBooking(any(Booking::class.java))
    }

    @Test
    fun `test createBooking with bookedItems`() {
        val renterId = 1
        val account = Account(accountId = renterId, username = "testUser", email = "test@example.com")
        val item1 = Item(itemId = 1, name = "Item 1", owner = account, status = ItemStatus.AVAILABLE)
        val item2 = Item(itemId = 2, name = "Item 2", owner = account, status = ItemStatus.AVAILABLE)
        val bookingDto = BookingDto(
            bookingId = 0,
            bookedItems = setOf(item1, item2),
            endDate = LocalDateTime.now(),
            renter = account
        )
        val booking =
            bookingDto.toEntity().copy(startDate = LocalDateTime.now(), status = BookingStatus.OPEN)

        `when`(accountProvider.getAccountById(renterId)).thenReturn(account)
        `when`(bookingProvider.saveBooking(any(Booking::class.java))).thenReturn(booking)

        val createdBookingDto = bookingService.createBooking(renterId, bookingDto)

        assertNotNull(createdBookingDto)
        assertEquals(bookingDto.bookedItems.size, createdBookingDto.bookedItems.size)
        assertEquals(BookingStatus.OPEN, createdBookingDto.status)
        verify(bookingProvider, times(1)).saveBooking(any(Booking::class.java))
    }

    @Test
    fun `test createBooking with empty bookedItems and bookedItemsIds`() {
        val renterId = 1
        val bookingDto = BookingDto(
            bookingId = 0,
            bookedItems = emptySet(),
            bookedItemsIds = emptySet(),
            endDate = LocalDateTime.now()
        )

        assertThrows(DomainException::class.java) {
            bookingService.createBooking(renterId, bookingDto)
        }
    }

    @Test
    fun `test closeBooking`() {
        val bookingId = 1
        val account = Account(accountId = 1, username = "testUser", email = "test@example.com")
        val item1 = Item(itemId = 1, name = "Item 1", owner = account, status = ItemStatus.AVAILABLE)
        val item2 = Item(itemId = 2, name = "Item 2", owner = account, status = ItemStatus.AVAILABLE)
        val booking = Booking(
            bookingId = bookingId,
            renter = account,
            bookedItems = setOf(item1, item2),
            status = BookingStatus.OPEN,
            endDate = LocalDateTime.now()
        )

        `when`(bookingProvider.getBookingById(bookingId)).thenReturn(booking)
        `when`(bookingProvider.updateBooking(booking)).thenReturn(booking)

        val closedBookingDto = bookingService.closeBooking(bookingId)

        assertNotNull(closedBookingDto)
        assertEquals(BookingStatus.CLOSE, closedBookingDto.status)
        verify(bookingProvider, times(1)).updateBooking(booking)
    }

    @Test
    fun `test getBookingById`() {
        val bookingId = 1
        val account = Account(accountId = 1, username = "testUser", email = "test@example.com")
        val booking =
            Booking(
                bookingId = bookingId,
                renter = account,
                bookedItems = emptySet(),
                status = BookingStatus.OPEN,
                endDate = LocalDateTime.now()
            )

        `when`(bookingProvider.getBookingById(bookingId)).thenReturn(booking)

        val bookingDto = bookingService.getBookingById(bookingId)

        assertNotNull(bookingDto)
        assertEquals(booking.bookingId, bookingDto.bookingId)
        assertEquals(booking.renter, bookingDto.renter)
        assertEquals(booking.status, bookingDto.status)
        verify(bookingProvider, times(1)).getBookingById(bookingId)
    }

    private fun BookingDto.toEntity(): Booking =
        Booking(
            bookingId = bookingId,
            renter = renter ?: throw DtoMapException("Illegal map dto to item"),
            startDate = startDate,
            endDate = endDate,
            status = status,
            description = description,
            bookedItems = bookedItems
        )

    private inline fun <reified T> any(type: Class<T>): T = Mockito.any(type)
}