package se.itmo.ru.sharedthings.integration

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import se.itmo.ru.sharedthings.AbstractIntegrationTest
import se.itmo.ru.sharedthings.dto.BookingDto
import se.itmo.ru.sharedthings.entity.Account
import se.itmo.ru.sharedthings.enums.BookingStatus
import se.itmo.ru.sharedthings.service.BookingService
import java.time.LocalDateTime

class BookingIntegrationTest : AbstractIntegrationTest() {

    @Autowired
    lateinit var bookingService: BookingService

    @Test
    fun `create booking should return 200`() {
        //given
        val renterId = 1
        val bookingDto =
            BookingDto(
                endDate = LocalDateTime.now().plusDays(2),
                bookedItemsIds = setOf(3, 4, 5)
            )

        //when
        mockMvc.perform(
            post("/api/booking/create")
                .param("renterId", renterId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingDto))
        ).andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.renter.accountId").value(renterId))
            .andExpect(jsonPath("$.bookedItems[0].itemId").value(3))
            .andExpect(jsonPath("$.bookedItems[1].itemId").value(4))
            .andExpect(jsonPath("$.bookedItems[2].itemId").value(5))

    }


    @Test
    fun `close booking should return 200`() {
        // given
        val bookingId = 3

        // when
        mockMvc.perform(
            post("/api/booking/close/$bookingId")
        ).andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.bookingId").value(bookingId))
            .andExpect(jsonPath("$.status").value(BookingStatus.CLOSE.name))

        // then
        jdbcTemplate.query(
            "SELECT status FROM booking WHERE booking_id = :bookingId",
            mapOf("bookingId" to bookingId)
        ) { rs, _ ->
            assertEquals(BookingStatus.CLOSE.name, rs.getString("status"))
        }

    }

    @Test
    fun `get booking by id should return 200`() {
        // given
        val renterId = 1
        val bookingDto = BookingDto(
            renter = Account(accountId = renterId, username = "test"),
            endDate = LocalDateTime.now().plusDays(2),
            bookedItemsIds = setOf(1, 2)
        )
        val createdBooking = bookingService.createBooking(renterId, bookingDto)

        // when
        mockMvc.perform(
            get("/api/booking/${createdBooking.bookingId}")
        ).andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.bookingId").value(createdBooking.bookingId))
            .andExpect(jsonPath("$.renter.accountId").value(renterId))
            .andExpect(jsonPath("$.endDate").value(createdBooking.endDate.toString()))
    }
}