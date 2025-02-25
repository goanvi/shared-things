package se.itmo.ru.common.dto.notification

import se.itmo.ru.common.kafka.Message
import java.util.UUID

data class BookingCreatedNotificationDto(
    val bookingId: UUID,
    val renterName: String,
    val renterId: UUID,
):Message
