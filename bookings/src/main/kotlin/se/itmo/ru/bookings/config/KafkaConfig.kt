package se.itmo.ru.bookings.config

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.*
import org.springframework.kafka.listener.DefaultErrorHandler
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.support.serializer.JsonSerializer
import org.springframework.util.backoff.FixedBackOff
import se.itmo.ru.common.kafka.Message

@Configuration
@EnableKafka
class KafkaConfig {

    @Bean
    fun producerFactory(@Value("\${app.kafka.url}") kafkaUrl: String): ProducerFactory<String, Message> {
        val config = mapOf<String, Any>(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaUrl,
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to JsonSerializer::class.java,
            JsonSerializer.ADD_TYPE_INFO_HEADERS to false
        )
        return DefaultKafkaProducerFactory(config)
    }

    @Bean
    fun kafkaTemplate(producerFactory: ProducerFactory<String, Message>): KafkaTemplate<String, Message> {
        return KafkaTemplate(producerFactory)
    }

    @Bean
    fun consumerFactory(
        @Value("\${app.kafka.url}") kafkaUrl: String,
        @Value("\${app.kafka.group-id}") groupId: String
    ): ConsumerFactory<String, Message> {
        val config = mapOf<String, Any>(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaUrl,
            ConsumerConfig.GROUP_ID_CONFIG to groupId,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to JsonDeserializer::class.java
        )
        return DefaultKafkaConsumerFactory(config)
    }

    @Bean
    fun kafkaListenerContainerFactory(
        consumerFactory: ConsumerFactory<String, Message>
    ): ConcurrentKafkaListenerContainerFactory<String, Message> {
        val factory =
            ConcurrentKafkaListenerContainerFactory<String, Message>()
        factory.consumerFactory = consumerFactory
        factory.setCommonErrorHandler(DefaultErrorHandler(FixedBackOff(5000, 10)))
        return factory
    }
}