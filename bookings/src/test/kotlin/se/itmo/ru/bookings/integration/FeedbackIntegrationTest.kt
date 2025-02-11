package se.itmo.ru.bookings.integration

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageRequest
import org.springframework.http.MediaType
import se.itmo.ru.bookings.AbstractIntegrationTest
import se.itmo.ru.common.dto.request.FeedbackRequest
import se.itmo.ru.common.dto.request.ModerateFeedbackRequest
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
            .jsonPath("$.item_id").isEqualTo(itemId)
            .jsonPath("$.booking_id").isEqualTo(bookingId)
            .jsonPath("$.rate").isEqualTo(feedbackRequest.rate)

        // then
        val result = r2dbcClient.sql("select * from feedback where item_id = :item_id and booking_id = :booking_id")
            .bindValues(
                mapOf(
                    "item_id" to feedbackRequest.itemId,
                    "booking_id" to feedbackRequest.bookingId
                )
            )
            .fetch()
            .one()
            .block()

        assertNotNull(result)
        assertEquals(feedbackRequest.rate, result?.get("rate"))
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
            .jsonPath("$.item_id").isEqualTo(itemId)
            .jsonPath("$.booking_id").isEqualTo(bookingId)
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
            .jsonPath("$.content[0].item_id").isEqualTo(itemId1)
            .jsonPath("$.content[1].item_id").isEqualTo(itemId2)
    }

    @Test
    fun `get unmoderated feedbacks should return 200`() {
        // given
        val itemId1 = "38345a1e-9ddf-48d9-b6dd-d6e78a798df2"
        val itemId2 = "85979ab8-c40e-4edb-bdd0-87c0bf90e905"
        val bookingId = "e9bfef4f-0082-428e-86c2-97f114893ec9"
        val pageable = PageRequest.of(0, 10)
        listOf(itemId1, itemId2).forEach { itemId ->
            r2dbcClient.sql(
                """
                update feedback
                set moderated = false
                where item_id = :itemId and booking_id = :bookingId
                """.trimIndent()
            )
                .bindValues(
                    mapOf(
                        "itemId" to UUID.fromString(itemId),
                        "bookingId" to UUID.fromString(bookingId),
                    )
                )
                .fetch()
                .all()
                .collectList()
                .block()
        }

        // when
        webTestClient
            .get()
            .uri {
                it.path("/api/feedback/unmoderated")
                    .queryParam("page", pageable.pageNumber)
                    .queryParam("size", pageable.pageSize)
                    .build()
            }
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.content").isArray
            .jsonPath("$.content[0].item_id").isEqualTo(itemId1)
            .jsonPath("$.content[1].item_id").isEqualTo(itemId2)
    }

    @Test
    fun `moderate feedbacks should return 200`() {
        // given
        val bookingId = "e9bfef4f-0082-428e-86c2-97f114893ec9"
        val itemId1 = "38345a1e-9ddf-48d9-b6dd-d6e78a798df2"
        val itemId2 = "85979ab8-c40e-4edb-bdd0-87c0bf90e905"
        listOf(itemId1, itemId2).forEach { itemId ->
            r2dbcClient.sql(
                """
                update feedback
                set moderated = false
                where item_id = :itemId and booking_id = :bookingId
                """.trimIndent()
            )
                .bindValues(
                    mapOf(
                        "itemId" to UUID.fromString(itemId),
                        "bookingId" to UUID.fromString(bookingId),
                    )
                )
                .fetch()
                .all()
                .collectList()
                .block()
        }


        val requestBody = setOf(
            ModerateFeedbackRequest(UUID.fromString(itemId1), UUID.fromString(bookingId)),
            ModerateFeedbackRequest(UUID.fromString(itemId2), UUID.fromString(bookingId))
        )

        // when
        webTestClient
            .post()
            .uri("/api/feedback/moderate")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestBody)
            .exchange()
            .expectStatus().isOk

        // then
        val result = r2dbcClient.sql(
            """
            select * from feedback f
            where (f.item_id, f.booking_id) in (
                select item_id, booking_id
                from unnest(array[:itemIds], array[:bookingIds]) with ordinality as t(item_id, booking_id, idx)
            )
        """.trimIndent()
        )
            .bindValues(
                mapOf(
                    "itemIds" to listOf(UUID.fromString(itemId1), UUID.fromString(itemId2)).toTypedArray(),
                    "bookingIds" to listOf(UUID.fromString(bookingId), UUID.fromString(bookingId)).toTypedArray(),
                )
            )
            .fetch()
            .all()
            .collectList()
            .block()
        result?.forEach { r ->
            assertEquals("true", r["moderated"].toString())
        }
    }
}


