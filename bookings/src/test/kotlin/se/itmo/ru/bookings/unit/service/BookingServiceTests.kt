package se.itmo.ru.bookings.unit.service

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.whenever
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import se.itmo.ru.bookings.exception.DomainException
import se.itmo.ru.bookings.repository.BookedItemsRepository
import se.itmo.ru.bookings.repository.BookingRepository
import se.itmo.ru.bookings.rest.client.AccountRestClient
import se.itmo.ru.bookings.service.BookingService
import se.itmo.ru.bookings.service.ItemService
import se.itmo.ru.common.dto.AccountDto
import se.itmo.ru.common.dto.request.BookingRequest
import java.time.LocalDateTime
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class BookingServiceTests {

    @Mock
    private lateinit var bookingRepository: BookingRepository

    @Mock
    private lateinit var bookedItemsRepository: BookedItemsRepository

    @Mock
    private lateinit var itemsService: ItemService

    @Mock
    private lateinit var accountRestClient: AccountRestClient

    @InjectMocks
    private lateinit var bookingService: BookingService

    @Test
    fun `createBooking() with empty bookedItems`(){
        //given
        val request = BookingRequest(
            renter = UUID.randomUUID(),
            bookedItems = emptySet(),
            endDate = LocalDateTime.now().plusDays(1),
            description = "Test"
        )


        // when
        val result = bookingService.createBooking(request)

        // then
        StepVerifier.create(result)
            .expectErrorMatches { it is DomainException && it.message == "Booked items can not be empty" }
            .verify()

        verify(accountRestClient, never()).getAccountById(anyOrNull())
        verify(itemsService, never()).existsById(anyOrNull())
    }

    @Test
    fun `createBooking() with doesn't exist itemId`(){
        // given
        val renterId = UUID.randomUUID()
        val itemId = UUID.randomUUID()
        val request = BookingRequest(
            renter = renterId,
            bookedItems = setOf(itemId),
            endDate = LocalDateTime.now().plusDays(1),
            description = "Test"
        )

        whenever(accountRestClient.getAccountById(renterId))
            .thenReturn(Mono.just(AccountDto(UUID.randomUUID(), "Test User")))
        whenever(itemsService.existsById(itemId))
            .thenReturn(Mono.just(false))

        // when
        val result = bookingService.createBooking(request)

        // then
        StepVerifier.create(result)
            .expectErrorMatches { it is DomainException && it.message == "Item with id $itemId does not exist" }
            .verify()

        verify(accountRestClient).getAccountById(renterId)
        verify(itemsService).existsById(itemId)
        verify(bookingRepository, never()).createBooking(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull())
    }
}