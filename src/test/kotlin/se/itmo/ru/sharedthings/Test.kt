package se.itmo.ru.sharedthings

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import se.itmo.ru.sharedthings.repository.*

class Test : AbstractIntegrationTest() {

    @Autowired
    lateinit var accountRepository: AccountRepository

    @Autowired
    lateinit var bookingRepository: BookingRepository

    @Autowired
    lateinit var feedbackRepository: FeedbackRepository

    @Autowired
    lateinit var itemRepository: ItemRepository

    @Autowired
    lateinit var wishlistItemRepository: WishlistItemRepository


    @Test
    fun test() {
    }
}