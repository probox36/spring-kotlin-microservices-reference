package com.buoyancy.common.config.kafka

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer
import org.springframework.kafka.listener.DefaultErrorHandler
import org.springframework.kafka.support.serializer.DeserializationException
import org.springframework.util.backoff.ExponentialBackOff

@Configuration
class KafkaErrorHandlerConfig(private val kafkaTemplate: KafkaTemplate<String, Any>) {

    @Bean
    fun deadLetterErrorHandler(kafkaTemplate: KafkaTemplate<String, Any>): DefaultErrorHandler {

        val log = KotlinLogging.logger {}
        val recoverer = DeadLetterPublishingRecoverer(kafkaTemplate)

        val errorHandler = DefaultErrorHandler({ record, exception ->
            log.error {
                "Final attempt failed for message from topic '${record.topic()}' " +
                        "with key '${record.key()}'. Sending to DLT. Cause: ${exception.message}"
            }
            recoverer.accept(record, exception)
        }, ExponentialBackOff(1000L, 2.0).apply {
            maxElapsedTime = 30000L
        })

        errorHandler.addRetryableExceptions(DeserializationException::class.java)

        return errorHandler
    }
}