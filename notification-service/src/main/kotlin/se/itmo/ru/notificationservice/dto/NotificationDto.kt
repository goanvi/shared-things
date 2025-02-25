package se.itmo.ru.notificationservice.dto

import java.util.UUID

data class NotificationDto(
    val userId: UUID,
    val message: String
)
