package se.itmo.ru.bookings.producer

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import se.itmo.ru.common.kafka.Message


@Service
class KafkaProducerService(
    private val kafkaTemplate: KafkaTemplate<String, Message>
) {

    fun sendMessage(topic: String, message: Message) {
        kafkaTemplate.send(topic, message)
    }
}