package se.itmo.ru.bookings.unit.service

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.mock.mockito.MockBean
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import se.itmo.ru.bookings.exception.DomainException
import se.itmo.ru.bookings.producer.KafkaProducerService
import se.itmo.ru.bookings.repository.BookedItemsRepository
import se.itmo.ru.bookings.repository.BookingRepository
import se.itmo.ru.bookings.rest.client.AccountRestClient
import se.itmo.ru.bookings.service.BookingService
import se.itmo.ru.bookings.service.ItemService
import se.itmo.ru.common.dto.AccountDto
import se.itmo.ru.common.dto.request.BookingRequest
import java.time.LocalDateTime
import java.util.*

@ExtendWith(MockitoExtension::class)
class BookingServiceTests {

    private var bookingRepository: BookingRepository = mock(BookingRepository::class.java)
    private var bookedItemsRepository: BookedItemsRepository = mock(BookedItemsRepository::class.java)
    private  var itemsService: ItemService = mock(ItemService::class.java)
    private  var accountRestClient: AccountRestClient = mock(AccountRestClient::class.java)
    private  var kafkaProducerService: KafkaProducerService = mock(KafkaProducerService::class.java)
    private var bookingCreatedTopic: String = ""
    private var bookingClosedTopic: String = ""

    private var bookingService: BookingService = BookingService(
        bookingRepository,
        bookedItemsRepository,
        itemsService,
        accountRestClient,
        kafkaProducerService,
        bookingCreatedTopic,
        bookingClosedTopic
    )

    @Test
    fun `createBooking() with empty bookedItems`() {
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
    fun `createBooking() with doesn't exist itemId`() {
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
        verify(bookingRepository, never()).createBooking(
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull(),
            anyOrNull()
        )
    }
}