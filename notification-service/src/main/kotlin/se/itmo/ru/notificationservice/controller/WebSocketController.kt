package se.itmo.ru.notificationservice.controller

import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import se.itmo.ru.common.dto.notification.ItemBookedNotificationDto
import se.itmo.ru.common.dto.notification.WishlistSuggestionNotificationDto
import se.itmo.ru.notificationservice.dto.NotificationDto
import se.itmo.ru.notificationservice.service.UserNotificationService
import java.util.logging.Logger
import kotlin.math.log

private val logger = Logger.getLogger("WebSocketController")

@Controller
class WebSocketController(
    private val userNotificationService: UserNotificationService
) {

    @MessageMapping("/wishlist/suggestion")
    fun handleWishlistSuggestionAddedNotification(
        wishlistSuggestionNotificationDto: WishlistSuggestionNotificationDto
    ) {
        val message = """
            Hello,
            You have a new wishlist suggestion
            Item name: ${wishlistSuggestionNotificationDto.itemName}
            Description: ${wishlistSuggestionNotificationDto.itemDescription}
        """.trimIndent()

        logger.warning(message)

//        userNotificationService.sendNotification(
//            NotificationDto(
//                userId = wishlistSuggestionNotificationDto.userId,
//                message = message
//            )
//        )
    }

    @MessageMapping("/wishlist/booked")
    fun handleItemBookedNotification(
        itemBookedNotificationDto: ItemBookedNotificationDto
    ) {
        val message = """
            Hello,
            Your wishlist "${itemBookedNotificationDto.wishlistTitle}" replaced to booking 
            Booked item: "${itemBookedNotificationDto.itemName}"
        """.trimIndent()

        logger.warning(message)


//        userNotificationService.sendNotification(
//            NotificationDto(
//                userId = itemBookedNotificationDto.userId,
//                message = message
//            )
//        )
    }
}