package se.itmo.ru.wishlists.service

import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Service
import se.itmo.ru.common.dto.notification.ItemBookedNotificationDto
import se.itmo.ru.common.dto.notification.WishlistSuggestionNotificationDto

@Service
class NotificationSenderService(
    private val wsStompSession: StompSession
) {

    fun sendItemBookedNotification(itemBookedNotificationDto: ItemBookedNotificationDto) {
        wsStompSession.send("/app/wishlist/booked", itemBookedNotificationDto)
    }

    fun sendNewWishlistSuggestionNotification(wishlistSuggestionNotificationDto: WishlistSuggestionNotificationDto) {
        wsStompSession.send("/app/wishlist/suggestion", wishlistSuggestionNotificationDto)
    }
}