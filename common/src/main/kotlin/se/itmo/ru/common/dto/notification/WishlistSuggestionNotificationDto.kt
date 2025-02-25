package se.itmo.ru.common.dto.notification

import se.itmo.ru.common.kafka.Message
import java.util.UUID

data class WishlistSuggestionNotificationDto(
    val userId: UUID,
    val itemName: String,
    val itemDescription: String
): Message
