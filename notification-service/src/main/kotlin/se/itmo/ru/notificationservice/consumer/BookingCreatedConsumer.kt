package se.itmo.ru.notificationservice.consumer

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Service
import se.itmo.ru.common.dto.notification.BookingCreatedNotificationDto
import java.util.logging.Logger

private val logger = Logger.getLogger("BookingCreatedConsumer")

@Service
class BookingCreatedConsumer {

    @KafkaListener(
        topics = ["\${app.kafka.topics.booking-created}"],
        properties = ["spring.json.value.default.type=se.itmo.ru.common.dto.notification.BookingCreatedNotificationDto"]
    )
    fun listenBookingCreatedMessage(@Payload bookingCreatedNotificationDto: BookingCreatedNotificationDto) {
        logger.warning(
            """
            |BookingCreatedConsumer
            |LOG: bookingId ${bookingCreatedNotificationDto.bookingId},
            |   renterName ${bookingCreatedNotificationDto.renterName},
            |   renterId ${bookingCreatedNotificationDto.renterId}
            """.trimMargin()
        )
    }
}