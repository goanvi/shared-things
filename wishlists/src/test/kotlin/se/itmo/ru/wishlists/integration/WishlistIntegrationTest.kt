package se.itmo.ru.wishlists.integration

import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageRequest
import org.springframework.http.MediaType
import se.itmo.ru.wishlists.AbstractIntegrationTest
import se.itmo.ru.wishlists.dto.request.UpdateWishlistItemRequest
import se.itmo.ru.wishlists.dto.request.WishlistItemRequest
import se.itmo.ru.wishlists.enum.WishlistStatus
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WishlistIntegrationTest : AbstractIntegrationTest() {

    @Test
    fun `create wishlist item should return 200`() {
        //given
        val wishlistItemRequest = WishlistItemRequest(
            owner = UUID.fromString("3baa3603-5f1d-458f-8f5b-d0509cc9f6a7"),
            title = "Test title",
            description = "Test description"
        )
        //when
        webTestClient
            .post()
            .uri("/api/wishlist/create")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(wishlistItemRequest)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.owner").isEqualTo(wishlistItemRequest.owner.toString())
            .jsonPath("$.title").isEqualTo(wishlistItemRequest.title)
            .jsonPath("$.description").isEqualTo(wishlistItemRequest.description!!)

        //then
        jdbcTemplate.query(
            "select * from wishlist_item where title = :title",
            mapOf("title" to wishlistItemRequest.title)
        ) { rs, _ ->
            assertEquals(wishlistItemRequest.description, rs.getString("description"))
        }
    }

    @Test
    fun `get wishlist by id should return 200`() {
        //given
        val wishlistId = "ae0e9479-78c1-4694-bd70-636dea818266"

        //when
        webTestClient
            .get()
            .uri("/api/wishlist/$wishlistId")
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.wishlistId").isEqualTo(wishlistId)
            .jsonPath("$.owner").isEqualTo("1e825e74-e60b-4244-bac7-cc25f3a7c7d4")
            .jsonPath("$.title").isEqualTo("Wishlist1")
            .jsonPath("$.description").isEqualTo("Description1")
    }

    @Test
    fun `get moderated wishlist by owner_id should return 200`() {
        //given
        val ownerId = "1e825e74-e60b-4244-bac7-cc25f3a7c7d4"
        val pageable = PageRequest.of(0, 10)

        //when
        webTestClient
            .get()
            .uri {
                it.path("/api/wishlist/owner/$ownerId")
                    .queryParam("page", pageable.pageNumber)
                    .queryParam("size", pageable.pageSize)
                    .build()
            }
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.content").isArray
            .jsonPath("$.content[0].wishlistId").isEqualTo("ae0e9479-78c1-4694-bd70-636dea818266")
            .jsonPath("$.content[1].wishlistId").isEqualTo("1e4e60cf-dedd-4c61-ae1c-e2c8bd18b3ca")
    }

    @Test
    fun `get wishlist suggestions should return 200`() {
        //given
        val wishlistId = "ae0e9479-78c1-4694-bd70-636dea818266"
        val pageable = PageRequest.of(0, 10)

        //when
        webTestClient
            .get()
            .uri {
                it.path("/api/wishlist/suggestions/$wishlistId")
                    .queryParam("page", pageable.pageNumber)
                    .queryParam("size", pageable.pageSize)
                    .build()
            }
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.content").isArray
            .jsonPath("$.content[0]").isEqualTo("baef6ba1-dc19-442e-a681-151c486190a4")
            .jsonPath("$.content[1]").isEqualTo("749cc399-524a-4018-aab3-5db347e3976c")
    }

    @Test
    fun `add item to wishlist suggestions should return 200`() {
        //given
        val itemId = "e747088e-b47a-4ef3-8351-fe8115304a31"
        val wishlistItemId = "31223b5a-68d6-45cc-a383-18f958158b3a"

        //when
        webTestClient
            .post()
            .uri {
                it.path("/api/wishlist/suggestions/add")
                    .queryParam("itemId", itemId)
                    .queryParam("wishlistId", wishlistItemId)
                    .build()
            }
            .exchange()
            .expectStatus().isOk
    }

    @Test
    fun `update wishlist should return 200`() {
        //given
        val wishlistId = "3a2f77d8-f5a4-48bb-86ba-67024e8bc0b7"
        val wishlistItemRequest = UpdateWishlistItemRequest(
            title = "updated title",
            description = "updated description"
        )

        //when
        webTestClient
            .put()
            .uri("/api/wishlist/$wishlistId")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(wishlistItemRequest)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.title").isEqualTo(wishlistItemRequest.title)
            .jsonPath("$.description").isEqualTo(wishlistItemRequest.description!!)

        //then
        jdbcTemplate.query(
            "select * from wishlist_item where wishlist_id = :wishlistId::UUID",
            mapOf("wishlistId" to wishlistId)
        ) { rs, _ ->
            assertEquals(wishlistItemRequest.description, rs.getString("description"))
        }
    }

    @Test
    fun `change wishlist status should return 200`() {
        //given
        val wishlistId = "3a2f77d8-f5a4-48bb-86ba-67024e8bc0b7"
        val wishlistStatus = WishlistStatus.CLOSE

        //when
        webTestClient
            .patch()
            .uri("/api/wishlist/status/$wishlistId")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(wishlistStatus)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.status").isEqualTo(wishlistStatus.name)

        //then
        jdbcTemplate.query(
            "select * from wishlist_item where wishlist_id = :wishlistId::UUID",
            mapOf("wishlistId" to wishlistId)
        ) { rs, _ ->
            assertEquals(wishlistStatus.name, rs.getString("status"))
        }
    }

    @Test
    fun `moderate wishlist items should return 200`() {
        //given
        val ids = listOf(
            UUID.fromString("3a2f77d8-f5a4-48bb-86ba-67024e8bc0b7"),
            UUID.fromString("3a2f77d8-f5a4-48bb-86ba-67023e8bc0b7")
        )

        //when
        webTestClient
            .post()
            .uri("/api/wishlist/moderate")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(ids)
            .exchange()
            .expectStatus().isOk

        //then
        jdbcTemplate.query(
            "select * from wishlist_item where wishlist_id in (:ids)",
            mapOf("ids" to ids)
        ) { rs, _ ->
            assertTrue { rs.getBoolean("moderated") }
        }
    }

    @Test
    fun `get unmoderated wishlists should return 200`() {
        //given
        val ids = listOf(
            UUID.fromString("3a2f77d8-f5a4-48bb-86ba-67023e8bc0b7"),
            UUID.fromString("3a2f77d8-f5a4-48bb-86ba-67024e8bc0b7")
        )
        val pageable = PageRequest.of(0, 10)
        jdbcTemplate.update(
            "update wishlist_item set moderated = true",
            mapOf("ids" to ids)
        )
        jdbcTemplate.update(
            "update wishlist_item set moderated = false where wishlist_id in (:ids)",
            mapOf("ids" to ids)
        )

        //when
        webTestClient
            .get()
            .uri {
                it.path("/api/wishlist/unmoderated")
                    .queryParam("page", pageable.pageNumber)
                    .queryParam("size", pageable.pageSize)
                    .build()
            }
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.content").isArray
            .jsonPath("$.content[0].wishlistId").isEqualTo(ids[0].toString())
            .jsonPath("$.content[1].wishlistId").isEqualTo(ids[1].toString())

    }
}