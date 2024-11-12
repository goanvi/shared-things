package se.itmo.ru.sharedthings.integration

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import se.itmo.ru.sharedthings.AbstractIntegrationTest
import se.itmo.ru.sharedthings.dto.ItemDto
import se.itmo.ru.sharedthings.service.ItemService
import java.util.*

class ItemIntegrationTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var itemService: ItemService

    @Test
    fun `create item should return 200`() {
        // given
        val ownerId = 1
        val itemDto = ItemDto(
            name = UUID.randomUUID().toString(),
            description = "Description"
        )

        // when
        mockMvc.perform(
            post("/api/item/create")
                .param("ownerId", ownerId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemDto))
        ).andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.name").value(itemDto.name))
            .andExpect(jsonPath("$.description").value(itemDto.description))

        // then
        jdbcTemplate.query(
            "SELECT name, description FROM item WHERE name = :name",
            mapOf("name" to itemDto.name)
        )
        { r, _ ->
            assertEquals(1, r.row)
            assertEquals(itemDto.name, r.getString("name"))
            assertEquals(itemDto.description, r.getString("description"))
        }
    }

    @Test
    fun `get moderated account items should return 200`() {
        // given
        val accountId = 3
        val pageable = PageRequest.of(0, 10)


        // when
        mockMvc.perform(
            get("/api/item/account")
                .param("accountId", accountId.toString())
                .param("page", pageable.pageNumber.toString())
                .param("size", pageable.pageSize.toString())
        ).andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content[0].itemId").value(4))
            .andExpect(jsonPath("$.content[1].itemId").value(5))
            .andExpect(jsonPath("$.content[2].itemId").value(6))
    }

    @Test
    fun `update account item should return 200`() {
        // given
        val accountId = 1
        val itemDto = ItemDto(
            name = UUID.randomUUID().toString(),
            description = "Description"
        )
        val createdItem = itemService.createItem(accountId, itemDto)
        val updatedItemDto = ItemDto(
            itemId = createdItem.itemId,
            name = UUID.randomUUID().toString(),
            description = "Updated Description"
        )

        // when
        mockMvc.perform(
            put("/api/item/$accountId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedItemDto))
        ).andExpect(status().isOk)

        // then
        jdbcTemplate.query(
            "SELECT name, description FROM item WHERE name = :name",
            mapOf("name" to updatedItemDto.name)
        )
        { r, _ ->
            assertEquals(1, r.row)
            assertEquals(updatedItemDto.name, r.getString("name"))
            assertEquals(updatedItemDto.description, r.getString("description"))
        }
    }
}