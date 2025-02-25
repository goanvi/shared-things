package se.itmo.ru.notificationservice.service

import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import se.itmo.ru.notificationservice.dto.NotificationDto

@Service
class UserNotificationService(
    private val messagingTemplate: SimpMessagingTemplate
) {

    fun sendNotification(notification: NotificationDto) {
        messagingTemplate.convertAndSendToUser(
            notification.userId.toString(),
            "/queue/notifications",
            notification.message
        )
    }
}