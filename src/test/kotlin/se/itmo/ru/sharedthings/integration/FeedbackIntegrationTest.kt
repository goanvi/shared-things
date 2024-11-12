package se.itmo.ru.sharedthings.integration

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import se.itmo.ru.sharedthings.AbstractIntegrationTest
import se.itmo.ru.sharedthings.dto.FeedbackDto
import se.itmo.ru.sharedthings.service.FeedbackService
import java.time.LocalDateTime
import kotlin.test.assertEquals

class FeedbackIntegrationTest : AbstractIntegrationTest() {

    @Autowired
    lateinit var feedbackService: FeedbackService

    @Test
    fun `create feedback should return 200`() {
        // given
        val feedbackDto = FeedbackDto(
            itemId = 7,
            bookingId = 1,
            title = "Test Feedback",
            description = "Test Description",
            date = LocalDateTime.now(),
            rate = 5
        )


        // when
        mockMvc.perform(
            post("/api/feedback/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(feedbackDto))
        ).andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.itemId").value(feedbackDto.itemId))
            .andExpect(jsonPath("$.bookingId").value(feedbackDto.bookingId))
            .andExpect(jsonPath("$.rate").value(feedbackDto.rate))

        // then
        jdbcTemplate.query(
            "select * from feedback where item_id = :item_id and booking_id = :booking_id",
            mapOf(
                "item_id" to feedbackDto.itemId,
                "booking_id" to feedbackDto.bookingId
            )
        ) { rs, _ ->
            assertEquals(1, rs.row)
            assertEquals(feedbackDto.rate, rs.getInt("rate"))
        }
    }

    @Test
    fun `get feedback by ids should return 200`() {
        // given
        val itemId = 8
        val bookingId = 3
        val feedbackDto = FeedbackDto(
            itemId = itemId,
            bookingId = bookingId,
            title = "Test Feedback",
            description = "Test Description",
            date = LocalDateTime.now(),
            rate = 5
        )
        val createdFeedback = feedbackService.createFeedback(feedbackDto)


        // when
        mockMvc.perform(
            get("/api/feedback/$itemId/$bookingId")
        ).andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.itemId").value(createdFeedback.itemId))
            .andExpect(jsonPath("$.bookingId").value(createdFeedback.bookingId))
            .andExpect(jsonPath("$.title").value(createdFeedback.title))
    }

    @Test
    fun `get moderated feedbacks should return 200`() {
        // given
        val itemId1 = 4
        val itemId2 = 5
        val bookingId = 1
        val feedbackDto1 = FeedbackDto(
            itemId = itemId1,
            bookingId = bookingId,
            title = "Test Feedback",
            description = "Test Description",
            date = LocalDateTime.now(),
            rate = 5
        )
        val feedbackDto2 = FeedbackDto(
            itemId = itemId2,
            bookingId = bookingId,
            title = "Test Feedback",
            description = "Test Description",
            date = LocalDateTime.now(),
            rate = 5
        )
        val createdFeedback1 = feedbackService.createFeedback(feedbackDto1)
        val createdFeedback2 = feedbackService.createFeedback(feedbackDto2)


        // when
        mockMvc.perform(
            get("/api/feedback/$bookingId")
        ).andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content[0].item.itemId").value(1))
            .andExpect(jsonPath("$.content[0].booking.bookingId").value(bookingId))
    }
}


