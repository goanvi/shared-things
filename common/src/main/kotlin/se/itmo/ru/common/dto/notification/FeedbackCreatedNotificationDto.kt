package se.itmo.ru.common.dto.notification

import se.itmo.ru.common.kafka.Message
import java.util.*

data class FeedbackCreatedNotificationDto(
    val itemId: UUID,
    val title: String,
    val description: String?,
    val rate: Int
) : Message