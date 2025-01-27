package se.itmo.ru.bookings.integration

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageRequest
import org.springframework.http.MediaType
import se.itmo.ru.bookings.AbstractIntegrationTest
import se.itmo.ru.bookings.dto.request.ItemRequest
import se.itmo.ru.bookings.dto.request.UpdateItemRequest
import java.util.*

class ItemIntegrationTest : AbstractIntegrationTest() {

    @Test
    fun `create item should return 200`() {
        // given
        val itemRequest = ItemRequest(
            name = UUID.randomUUID().toString(),
            description = "Description",
            owner = UUID.fromString("3baa3603-5f1d-458f-8f5b-d0509cc9f6a7")
        )

        // when
        webTestClient
            .post()
            .uri("/api/item/create")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(itemRequest)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.name").isEqualTo(itemRequest.name)
            .jsonPath("$.description").isEqualTo(itemRequest.description!!)

        // then
        r2dbcClient.sql("SELECT name, description FROM item WHERE name = :name")
            .bindValues(mapOf("name" to itemRequest.name))
            .fetch()
            .all()
            .doOnEach { r ->
                assertEquals(itemRequest.name, r.get()?.get("name").toString())
                assertEquals(itemRequest.description, r.get()?.get("description").toString())
            }
            .count()
            .map { assertEquals(1, it.toLong()) }
            .subscribe()

    }

    @Test
    fun `get moderated account items should return 200`() {
        // given
        val accountId = "394d83ef-480d-44d4-947c-5194c9e53b6b"
        val pageable = PageRequest.of(0, 10)


        // when
        webTestClient
            .get()
            .uri {
                it.path("/api/item/account/{id}")
                    .queryParam("page", pageable.pageNumber)
                    .queryParam("size", pageable.pageSize)
                    .build(accountId)
            }
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.content").isArray
            .jsonPath("$.content[0].itemId").isEqualTo("749cc399-524a-4018-aab3-5db347e3976c")
            .jsonPath("$.content[1].itemId").isEqualTo("38345a1e-9ddf-48d9-b6dd-d6e78a798df2")
            .jsonPath("$.content[2].itemId").isEqualTo("85979ab8-c40e-4edb-bdd0-87c0bf90e905")
    }

    @Test
    fun `update account item should return 200`() {
        // given
        val itemId = "baef6ba1-dc19-442e-a681-151c486190a4"
        val itemRequest = UpdateItemRequest(
            name = "user1",
            description = "Updated Description",
        )

        // when
        webTestClient
            .put()
            .uri("/api/item/$itemId")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(itemRequest)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.name").isEqualTo(itemRequest.name)
            .jsonPath("$.description").isEqualTo(itemRequest.description!!)

        // then
        r2dbcClient.sql("SELECT name, description FROM item WHERE name = :name")
            .bindValues(mapOf("name" to itemRequest.name))
            .fetch()
            .all()
            .doOnEach { r ->
                assertEquals(itemRequest.name, r.get()?.get("name").toString())
                assertEquals(itemRequest.description, r.get()?.get("description").toString())
            }
            .count()
            .map { assertEquals(1, it.toLong()) }
            .subscribe()
    }
}