package se.itmo.ru.bookings.integration

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.domain.PageRequest
import org.springframework.http.MediaType
import reactor.core.publisher.Mono
import se.itmo.ru.bookings.AbstractIntegrationTest
import se.itmo.ru.common.dto.request.ItemRequest
import se.itmo.ru.common.dto.request.UpdateItemRequest
import se.itmo.ru.common.ItemStatus
import se.itmo.ru.bookings.rest.client.AccountRestClient
import se.itmo.ru.common.dto.AccountDto
import java.util.*

class ItemIntegrationTest : AbstractIntegrationTest() {

    @MockBean
    lateinit var accountRestClient: AccountRestClient

    @Test
    fun `create item should return 200`() {
        // given
        val itemRequest = ItemRequest(
            name = UUID.randomUUID().toString(),
            description = "Description",
            owner = UUID.fromString("3baa3603-5f1d-458f-8f5b-d0509cc9f6a7")
        )
        `when`(accountRestClient.getAccountById(itemRequest.owner))
            .thenReturn(Mono.just(AccountDto(itemRequest.owner, "Owner")))

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
        val result = r2dbcClient.sql("SELECT name, description FROM item WHERE name = :name")
            .bindValues(mapOf("name" to itemRequest.name))
            .fetch()
            .all()
            .collectList()
            .block()
        result?.forEach { r ->
            assertEquals(itemRequest.name, r.get("name").toString())
            assertEquals(itemRequest.description, r.get("description").toString())
        }
        assertEquals(1, result?.size?.toLong())
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
            .jsonPath("$.content[0].item_id").isEqualTo("749cc399-524a-4018-aab3-5db347e3976c")
            .jsonPath("$.content[1].item_id").isEqualTo("38345a1e-9ddf-48d9-b6dd-d6e78a798df2")
            .jsonPath("$.content[2].item_id").isEqualTo("85979ab8-c40e-4edb-bdd0-87c0bf90e905")
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
        val result = r2dbcClient.sql("update item set moderated = true where name = :name returning *")
            .bindValues(mapOf("name" to itemRequest.name))
            .fetch()
            .one()
            .block()

        assertNotNull(result)
        assertEquals(itemRequest.name, result?.get("name").toString())
        assertEquals(itemRequest.description, result?.get("description").toString())
    }

    @Test
    fun `moderate items should return 200`() {
        // given
        val itemId1 = "c27a5d4a-d1d3-4759-9249-a91049949cd9"
        val itemId2 = "16a79877-1665-4cbc-a2ac-69500c30ccac"

        val requestBody = setOf(itemId1, itemId2)

        // when
        webTestClient
            .post()
            .uri("/api/item/moderate")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestBody)
            .exchange()
            .expectStatus().isOk

        // then
        val result = r2dbcClient.sql("select * from item where item_id in (:ids)")
            .bindValues(
                mapOf(
                    "ids" to requestBody.map { UUID.fromString(it) },
                )
            )
            .fetch()
            .all()
            .collectList()
            .block()

        result?.forEach { r ->
            assertEquals(true, r["moderated"]?.let { it as Boolean })
        }

    }

    @Test
    fun `update item status should return 200`() {

        //given
        val itemId = "16a79877-1665-4cbc-a2ac-69500c30ccac"
        val itemStatus = ItemStatus.BOOKED

        //when
        webTestClient
            .patch()
            .uri("/api/item/status/$itemId")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(itemStatus)
            .exchange()
            .expectStatus().isOk

        // then
        val result = r2dbcClient.sql("select * from item where item_id = :itemId")
            .bindValues(mapOf("itemId" to UUID.fromString(itemId)))
            .fetch()
            .one()
            .block()

        assertNotNull(result)
        assertEquals(ItemStatus.BOOKED.name, result?.get("status").toString())

    }

    @Test
    fun `get unmoderated items should return 200`() {
        // given
        val itemId1 = "c27a5d4a-d1d3-4759-9249-a91049949cd9"
        val itemId2 = "16a79877-1665-4cbc-a2ac-69500c30ccac"
        val pageable = PageRequest.of(0, 10)
        r2dbcClient.sql("update item set moderated = false where item_id in (:ids)")
            .bindValues(mapOf("ids" to listOf(UUID.fromString(itemId1), UUID.fromString(itemId2))))
            .fetch()
            .one()
            .block()

        // when
        webTestClient
            .get()
            .uri {
                it.path("/api/item/unmoderated")
                    .queryParam("page", pageable.pageNumber)
                    .queryParam("size", pageable.pageSize)
                    .build()
            }
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectHeader().valueEquals("X-Total-Count", 2)
            .expectBody()
            .jsonPath("$.content").isArray
            .jsonPath("$.content[0].item_id").isEqualTo(itemId1)
            .jsonPath("$.content[1].item_id").isEqualTo(itemId2)
    }
}