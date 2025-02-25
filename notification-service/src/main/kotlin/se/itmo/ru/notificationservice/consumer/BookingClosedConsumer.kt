package se.itmo.ru.notificationservice.consumer

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Service
import se.itmo.ru.common.dto.notification.BookingClosedNotificationDto
import java.util.logging.Logger

private val logger = Logger.getLogger("BookingClosedConsumer")

@Service
class BookingClosedConsumer {

    @KafkaListener(
        topics = ["\${app.kafka.topics.booking-closed}"],
        properties = ["spring.json.value.default.type=se.itmo.ru.common.dto.notification.BookingClosedNotificationDto"]
    )
    fun listenBookingClosedMessage(@Payload bookingClosedNotificationDto: BookingClosedNotificationDto) {
        logger.warning(
            """
            |BookingClosedConsumer
            |LOG: bookingId ${bookingClosedNotificationDto.bookingId},
            """.trimMargin()
        )
    }
}