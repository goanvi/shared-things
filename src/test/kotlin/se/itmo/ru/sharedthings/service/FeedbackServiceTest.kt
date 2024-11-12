package se.itmo.ru.sharedthings.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import se.itmo.ru.sharedthings.dto.FeedbackDto
import se.itmo.ru.sharedthings.entity.Account
import se.itmo.ru.sharedthings.entity.Booking
import se.itmo.ru.sharedthings.entity.Feedback
import se.itmo.ru.sharedthings.entity.Item
import se.itmo.ru.sharedthings.enums.BookingStatus
import se.itmo.ru.sharedthings.enums.ItemStatus
import se.itmo.ru.sharedthings.exceptions.DomainException
import se.itmo.ru.sharedthings.exceptions.DtoMapException
import se.itmo.ru.sharedthings.provider.BookingRepositoryProvider
import se.itmo.ru.sharedthings.provider.FeedbackRepositoryProvider
import se.itmo.ru.sharedthings.provider.ItemRepositoryProvider
import java.time.LocalDateTime

class FeedbackServiceTest {

    private lateinit var feedbackProvider: FeedbackRepositoryProvider
    private lateinit var itemProvider: ItemRepositoryProvider
    private lateinit var bookingProvider: BookingRepositoryProvider
    private lateinit var feedbackService: FeedbackService

    @BeforeEach
    fun setUp() {
        feedbackProvider = mock(FeedbackRepositoryProvider::class.java)
        itemProvider = mock(ItemRepositoryProvider::class.java)
        bookingProvider = mock(BookingRepositoryProvider::class.java)
        feedbackService = FeedbackService(feedbackProvider, itemProvider, bookingProvider)
    }

    @Test
    fun `test createFeedback with itemId and bookingId`() {
        val itemId = 1
        val bookingId = 1
        val owner = Account(accountId = 1, username = "name")
        val item = Item(itemId = itemId, name = "Test Item", owner = owner, status = ItemStatus.AVAILABLE)
        val booking = Booking(
            bookingId = bookingId,
            renter = owner,
            status = BookingStatus.OPEN,
            bookedItems = setOf(item),
            endDate = LocalDateTime.now()
        )
        val feedbackDto = FeedbackDto(
            itemId = itemId,
            bookingId = bookingId,
            title = "Test title",
            rate = 4,
            item = item,
            booking = booking,
        )
        val feedback =
            feedbackDto.toEntity().copy(date = LocalDateTime.now(), moderated = false)

        `when`(itemProvider.getItemById(itemId)).thenReturn(item)
        `when`(bookingProvider.getBookingById(bookingId)).thenReturn(booking)
        `when`(feedbackProvider.saveFeedback(any(Feedback::class.java))).thenReturn(feedback)

        val createdFeedbackDto = feedbackService.createFeedback(feedbackDto)

        assertNotNull(createdFeedbackDto)
        assertEquals(item, createdFeedbackDto.item)
        assertEquals(booking, createdFeedbackDto.booking)
        assertFalse(createdFeedbackDto.moderated)
        verify(feedbackProvider, times(1)).saveFeedback(any(Feedback::class.java))
    }

    @Test
    fun `test createFeedback with null item and booking`() {
        val feedbackDto =
            FeedbackDto(itemId = null, bookingId = null, item = null, booking = null, title = "null", rate = 2)

        assertThrows(DomainException::class.java) {
            feedbackService.createFeedback(feedbackDto)
        }
    }

    @Test
    fun `test getAllUnmoderatedFeedback`() {
        val pageable = PageRequest.of(0, 10)
        val feedbacks = listOf(
            createFeedback(itemId = 1, bookingId = 1),
            createFeedback(itemId = 2, bookingId = 2),
        )
        val page = PageImpl(feedbacks, pageable, feedbacks.size.toLong())

        `when`(feedbackProvider.getAllUnmoderatedFeedback(pageable)).thenReturn(page)

        val result = feedbackService.getAllUnmoderatedFeedback(pageable)

        assertNotNull(result)
        assertEquals(feedbacks.size, result.content.size)
        assertEquals(feedbacks.map { it.toDto() }, result.content)
        verify(feedbackProvider, times(1)).getAllUnmoderatedFeedback(pageable)
    }

    @Test
    fun `test setFeedbackAsModerated`() {
        val feedbackIds = setOf(Pair(1, 1), Pair(2, 2))

        `when`(feedbackProvider.setFeedbacksAsModerated(feedbackIds)).thenReturn(feedbackIds.size)

        val result = feedbackService.setFeedbackAsModerated(feedbackIds)

        assertEquals(feedbackIds.size, result)
        verify(feedbackProvider, times(1)).setFeedbacksAsModerated(feedbackIds)
    }

    @Test
    fun `test getFeedbackByIds`() {
        val itemId = 1
        val bookingId = 1
        val feedback = createFeedback(itemId = itemId, bookingId = bookingId)

        `when`(feedbackProvider.getFeedbackByIds(itemId, bookingId)).thenReturn(feedback)

        val result = feedbackService.getFeedbackByIds(itemId, bookingId)

        assertNotNull(result)
        assertEquals(feedback.toDto(), result)
        verify(feedbackProvider, times(1)).getFeedbackByIds(itemId, bookingId)
    }

    @Test
    fun `test getAllModeratedFeedBackByBookingId`() {
        val bookingId = 1
        val pageable = PageRequest.of(0, 10)
        val feedbacks = listOf(
            createFeedback(itemId = 1, bookingId = bookingId),
            createFeedback(itemId = 2, bookingId = bookingId)
        )
        val page = PageImpl(feedbacks, pageable, feedbacks.size.toLong())

        `when`(feedbackProvider.getAllModeratedFeedBackByBookingId(bookingId, pageable)).thenReturn(page)

        val result = feedbackService.getAllModeratedFeedBackByBookingId(bookingId, pageable)

        assertNotNull(result)
        assertEquals(feedbacks.size, result.content.size)
        assertEquals(feedbacks.map { it.toDto() }, result.content)
        verify(feedbackProvider, times(1)).getAllModeratedFeedBackByBookingId(bookingId, pageable)
    }

    private fun createFeedback(
        itemId: Int = 1,
        bookingId: Int = 1,
        ownerId: Int = 1,
        renterId: Int = 2,
    ): Feedback {
        val item = Item(
            itemId = itemId,
            name = "Test Item $itemId",
            owner = Account(accountId = ownerId, username = "user $ownerId"),
            status = ItemStatus.BOOKED
        )
        val booking = Booking(
            bookingId = bookingId,
            renter = Account(accountId = renterId, username = "user2 $renterId"),
            endDate = LocalDateTime.now(),
            status = BookingStatus.OPEN,
            bookedItems = setOf(item)
        )
        return Feedback(item = item, booking = booking, title = "Test feedback", rate = 1)
    }

    private fun FeedbackDto.toEntity(): Feedback =
        Feedback(
            item = item ?: throw DtoMapException("Illegal map dto to item"),
            booking = booking ?: throw DtoMapException("Illegal map dto to booking"),
            title = title,
            description = description,
            date = date,
            rate = rate,
            moderated = moderated
        )

    private fun Feedback.toDto(): FeedbackDto =
        FeedbackDto(
            item = item,
            itemId = item.itemId,
            booking = booking,
            bookingId = booking.bookingId,
            title = title,
            description = description,
            date = date,
            rate = rate,
            moderated = moderated
        )

    private inline fun <reified T> any(type: Class<T>): T = Mockito.any(type)
}