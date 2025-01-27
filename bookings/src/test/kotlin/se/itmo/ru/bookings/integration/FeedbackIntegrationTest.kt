package se.itmo.ru.bookings.integration

import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageRequest
import org.springframework.http.MediaType
import se.itmo.ru.bookings.AbstractIntegrationTest
import se.itmo.ru.bookings.dto.request.FeedbackRequest
import java.util.*
import kotlin.test.assertEquals

class FeedbackIntegrationTest : AbstractIntegrationTest() {

    @Test
    fun `create feedback should return 200`() {
        // given
        val itemId = "3a2f77d8-f5a4-48bb-86ba-67024e8bc0b7"
        val bookingId = "1d70f3ff-9d83-4641-844f-0c85b9a7fb2e"
        val feedbackRequest = FeedbackRequest(
            itemId = UUID.fromString(itemId),
            bookingId = UUID.fromString(bookingId),
            title = "Test Feedback",
            description = "Test Description",
            rate = 5
        )

        // when
        webTestClient
            .post()
            .uri("/api/feedback/create")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(feedbackRequest)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.itemId").isEqualTo(itemId)
            .jsonPath("$.bookingId").isEqualTo(bookingId)
            .jsonPath("$.rate").isEqualTo(feedbackRequest.rate)

        // then
        r2dbcClient.sql("select * from feedback where item_id = :item_id and booking_id = :booking_id")
            .bindValues(
                mapOf(
                    "item_id" to feedbackRequest.itemId,
                    "booking_id" to feedbackRequest.bookingId
                )
            )
            .fetch()
            .one()
            .doOnSuccess { r ->
                assertEquals(feedbackRequest.rate, r["rate"])
            }
            .subscribe()
    }

    @Test
    fun `get feedback by ids should return 200`() {
        // given
        val itemId = "baef6ba1-dc19-442e-a681-151c486190a4"
        val bookingId = "1d70f3ff-9d83-4641-844f-0c85b9a7fb2e"

        // when
        webTestClient
            .get()
            .uri("/api/feedback/$itemId/$bookingId")
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.itemId").isEqualTo(itemId)
            .jsonPath("$.bookingId").isEqualTo(bookingId)
            .jsonPath("$.title").isEqualTo("Feedback1")
    }

    @Test
    fun `get moderated feedbacks should return 200`() {
        // given
        val itemId1 = "5bbf3def-9503-41c9-8a04-b9420bccf3da"
        val itemId2 = "bd1c8579-7292-4099-892a-d75efd6164bf"
        val bookingId = "53c7b205-c4f4-495e-83bd-9a4b1269785f"
        val pageable = PageRequest.of(0, 10)

        // when
        webTestClient
            .get()
            .uri {
                it.path("/api/feedback/moderated/$bookingId")
                    .queryParam("page", pageable.pageNumber)
                    .queryParam("size", pageable.pageSize)
                    .build()
            }
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.content").isArray
            .jsonPath("$.content[0].itemId").isEqualTo(itemId1)
            .jsonPath("$.content[1].itemId").isEqualTo(itemId2)
    }
}


