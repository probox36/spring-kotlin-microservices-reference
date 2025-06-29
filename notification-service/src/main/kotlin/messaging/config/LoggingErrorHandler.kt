package com.buoyancy.notification.messaging.config

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.errors.RecordDeserializationException
import org.slf4j.LoggerFactory
import org.springframework.kafka.listener.CommonErrorHandler
import org.springframework.kafka.listener.MessageListenerContainer
import org.springframework.stereotype.Component

@Component
class LoggingErrorHandler : CommonErrorHandler {
    private val log = LoggerFactory.getLogger(this::class.java)

    override fun handleOne(
        exception: Exception,
        record: ConsumerRecord<*, *>,
        consumer: org.apache.kafka.clients.consumer.Consumer<*, *>,
        container: MessageListenerContainer
    ) : Boolean {
        log.error("Kafka message processing exception. Topic: ${record.topic()}; Partition: ${record.partition()}; " +
                "Offset: ${record.offset()}; Key: ${record.key()}; Value: ${record.value()}; " +
                "Exception type: ${exception.javaClass.simpleName}; Exception message: ${exception.message};" +
                "\nStack trace: ${exception.stackTraceToString()}")
        if (exception is RecordDeserializationException) {
            consumer.seek(exception.topicPartition(), exception.offset() + 1L)
            consumer.commitSync()
        }
        return true
    }

    override fun handleOtherException(
        exception: Exception,
        consumer: org.apache.kafka.clients.consumer.Consumer<*, *>,
        container: MessageListenerContainer,
        batchListener: Boolean
    ) {
        log.error("Kafka container exception. Exception type: ${exception.javaClass.simpleName}; " +
                "Exception message: ${exception.message}")
    }
}