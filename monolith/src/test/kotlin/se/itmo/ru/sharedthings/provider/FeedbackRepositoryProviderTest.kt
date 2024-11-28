package se.itmo.ru.sharedthings.provider

import jakarta.persistence.EntityExistsException
import jakarta.persistence.EntityNotFoundException
import jakarta.persistence.PersistenceException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import se.itmo.ru.sharedthings.entity.Account
import se.itmo.ru.sharedthings.entity.Booking
import se.itmo.ru.sharedthings.entity.Feedback
import se.itmo.ru.sharedthings.entity.Item
import se.itmo.ru.sharedthings.enums.BookingStatus
import se.itmo.ru.sharedthings.enums.ItemStatus
import se.itmo.ru.sharedthings.repository.BookingRepository
import se.itmo.ru.sharedthings.repository.FeedbackRepository
import se.itmo.ru.sharedthings.repository.ItemRepository
import java.time.LocalDateTime

class FeedbackRepositoryProviderTest {

    private lateinit var feedbackRepository: FeedbackRepository
    private lateinit var itemRepository: ItemRepository
    private lateinit var bookingRepository: BookingRepository
    private lateinit var feedbackRepositoryProvider: FeedbackRepositoryProvider

    @BeforeEach
    fun setUp() {
        feedbackRepository = mock(FeedbackRepository::class.java)
        itemRepository = mock(ItemRepository::class.java)
        bookingRepository = mock(BookingRepository::class.java)
        feedbackRepositoryProvider = FeedbackRepositoryProvider(feedbackRepository, itemRepository, bookingRepository)
    }

    @Test
    fun `test saveFeedback with valid feedback`() {
        val feedback = createFeedback()

        `when`(itemRepository.existsById(anyInt())).thenReturn(true)
        `when`(bookingRepository.existsById(anyInt())).thenReturn(true)
        `when`(feedbackRepository.existsByItemAndBooking(feedback.item, feedback.booking)).thenReturn(
            false
        )
        `when`(feedbackRepository.save(any(Feedback::class.java))).thenReturn(feedback)

        val savedFeedback = feedbackRepositoryProvider.saveFeedback(feedback)

        assertNotNull(savedFeedback)
        assertEquals(feedback, savedFeedback)
        verify(feedbackRepository, times(1)).save(feedback)
    }

    @Test
    fun `test saveFeedback with non-existing item`() {
        val feedback = createFeedback()

        `when`(itemRepository.existsById(anyInt())).thenReturn(false)

        assertThrows(PersistenceException::class.java) {
            feedbackRepositoryProvider.saveFeedback(feedback)
        }
    }

    @Test
    fun `test saveFeedback with non-existing booking`() {
        val feedback = createFeedback()

        `when`(itemRepository.existsById(anyInt())).thenReturn(true)
        `when`(bookingRepository.existsById(anyInt())).thenReturn(false)

        assertThrows(PersistenceException::class.java) {
            feedbackRepositoryProvider.saveFeedback(feedback)
        }
    }

    @Test
    fun `test saveFeedback with existing feedback`() {
        val feedback = createFeedback()

        `when`(itemRepository.existsById(anyInt())).thenReturn(true)
        `when`(bookingRepository.existsById(anyInt())).thenReturn(true)
        `when`(feedbackRepository.existsByItemAndBooking(feedback.item, feedback.booking)).thenReturn(
            true
        )

        assertThrows(EntityExistsException::class.java) {
            feedbackRepositoryProvider.saveFeedback(feedback)
        }
    }

    @Test
    fun `test getAllUnmoderatedFeedback`() {
        val pageable = PageRequest.of(0, 10)
        val feedbacks = listOf(
            createFeedback(),
            createFeedback()
        )
        val page = PageImpl(feedbacks, pageable, feedbacks.size.toLong())

        `when`(feedbackRepository.findAllByModerated(false, pageable)).thenReturn(page)

        val result = feedbackRepositoryProvider.getAllUnmoderatedFeedback(pageable)

        assertNotNull(result)
        assertEquals(feedbacks.size, result.content.size)
        assertEquals(feedbacks, result.content)
    }

    @Test
    fun `test setFeedbacksAsModerated`() {
        val feedbackIds = setOf(Pair(1, 1), Pair(2, 2))

        `when`(feedbackRepository.setFeedbacksAsModerated(feedbackIds)).thenReturn(feedbackIds.size)

        val result = feedbackRepositoryProvider.setFeedbacksAsModerated(feedbackIds)

        assertEquals(feedbackIds.size, result)
        verify(feedbackRepository, times(1)).setFeedbacksAsModerated(feedbackIds)
    }

    @Test
    fun `test getFeedbackByIds with existing feedback`() {
        val itemId = 1
        val bookingId = 1
        val feedback = createFeedback()

        `when`(feedbackRepository.findFeedbackByItemIdAndBookingId(itemId, bookingId)).thenReturn(feedback)

        val result = feedbackRepositoryProvider.getFeedbackByIds(itemId, bookingId)

        assertNotNull(result)
        assertEquals(feedback, result)
    }

    @Test
    fun `test getFeedbackByIds with non-existing feedback`() {
        val itemId = 1
        val bookingId = 1

        `when`(feedbackRepository.findFeedbackByItemIdAndBookingId(itemId, bookingId)).thenReturn(null)

        assertThrows(EntityNotFoundException::class.java) {
            feedbackRepositoryProvider.getFeedbackByIds(itemId, bookingId)
        }
    }

    @Test
    fun `test getAllModeratedFeedBackByBookingId`() {
        val bookingId = 1
        val pageable = PageRequest.of(0, 10)
        val feedbacks = listOf(
            createFeedback(),
            createFeedback()
        )
        val page = PageImpl(feedbacks, pageable, feedbacks.size.toLong())

        `when`(feedbackRepository.findAllModeratedFeedbackByBookingId(bookingId, pageable)).thenReturn(page)

        val result = feedbackRepositoryProvider.getAllModeratedFeedBackByBookingId(bookingId, pageable)

        assertNotNull(result)
        assertEquals(feedbacks.size, result.content.size)
        assertEquals(feedbacks, result.content)
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
}