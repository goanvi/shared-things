package se.itmo.ru.bookings.integration

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import reactor.core.publisher.Mono
import se.itmo.ru.bookings.AbstractIntegrationTest
import se.itmo.ru.common.dto.request.BookingRequest
import se.itmo.ru.common.BookingStatus
import se.itmo.ru.bookings.rest.client.AccountRestClient
import se.itmo.ru.common.dto.AccountDto
import java.time.LocalDateTime
import java.util.*

class BookingIntegrationTest : AbstractIntegrationTest() {

    @MockBean
    lateinit var accountRestClient: AccountRestClient

    @Test
    fun `create booking should return 200`() {
        //given
        val renterId = "3baa3603-5f1d-458f-8f5b-d0509cc9f6a7"
        val item1 = "bd1c8579-7292-4099-892a-d75efd6164bf"
        val item2 = "749cc399-524a-4018-aab3-5db347e3976c"
        val item3 = "38345a1e-9ddf-48d9-b6dd-d6e78a798df2"
        val bookingRequest =
            BookingRequest(
                renter = UUID.fromString(renterId),
                endDate = LocalDateTime.now().plusDays(2),
                description = "Description",
                bookedItems = setOf(
                    UUID.fromString(item1),
                    UUID.fromString(item2),
                    UUID.fromString(item3)
                )
            )
        `when`(accountRestClient.getAccountById(UUID.fromString(renterId)))
            .thenReturn(Mono.just(AccountDto(UUID.fromString(renterId), "Renter")))

        //when
        webTestClient
            .post()
            .uri("/booking/create")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(bookingRequest)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.renter").isEqualTo(renterId)
            .jsonPath("$.description").isEqualTo(bookingRequest.description!!)
            .jsonPath("\$.booked_items[0]").isEqualTo(item1)
            .jsonPath("\$.booked_items[1]").isEqualTo(item2)
            .jsonPath("\$.booked_items[2]").isEqualTo(item3)
    }

    @Test
    fun `close booking should return 200`() {
        // given
        val bookingId = "e9bfef4f-0082-428e-86c2-97f114893ec9"

        // when
        webTestClient
            .post()
            .uri("/booking/close/$bookingId")
            .exchange()
            .expectStatus().isOk

        // then
        val result = r2dbcClient.sql("SELECT status FROM booking WHERE booking_id = :bookingId")
            .bindValues(mapOf("bookingId" to UUID.fromString(bookingId)))
            .fetch()
            .one()
            .block()

        assertNotNull(result)
        assertEquals(BookingStatus.CLOSE.name, result?.get("status").toString())
    }

    @Test
    fun `get booking by id should return 200`() {
        // given
        val bookingId = "1d70f3ff-9d83-4641-844f-0c85b9a7fb2e"
        val renterId = "1e825e74-e60b-4244-bac7-cc25f3a7c7d4"

        // when
        webTestClient
            .get()
            .uri("/booking/$bookingId")
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.booking_id").isEqualTo(bookingId)
            .jsonPath("$.renter").isEqualTo(renterId)
    }
}