package se.itmo.ru.notificationservice.consumer

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Service
import se.itmo.ru.common.dto.notification.FeedbackCreatedNotificationDto
import java.util.logging.Logger

private val logger = Logger.getLogger("FeedbackCreatedConsumer")

@Service
class FeedbackCreatedConsumer {

    @KafkaListener(
        topics = ["\${app.kafka.topics.feedback-created}"],
        properties = ["spring.json.value.default.type=se.itmo.ru.common.dto.notification.FeedbackCreatedNotificationDto"]
    )
    fun listenFeedbackCreatedMessage(@Payload feedbackCreatedNotificationDto: FeedbackCreatedNotificationDto) {
        logger.warning(
            """
            |FeedbackCreatedConsumer
            |LOG: itemId ${feedbackCreatedNotificationDto.itemId},
            |   title ${feedbackCreatedNotificationDto.title},
            |   description ${feedbackCreatedNotificationDto.description}
            |   rate ${feedbackCreatedNotificationDto.rate}
            """.trimMargin()
        )
    }
}