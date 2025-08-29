package com.buoyancy.common.config.kafka

import com.buoyancy.common.model.enums.TopicNames
import org.apache.kafka.clients.admin.NewTopic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder

@Configuration
class KafkaTopicConfig {

    @Bean
    fun ordersTopic(): NewTopic {
        return TopicBuilder.name(TopicNames.ORDER)
            .partitions(3)
            .replicas(1)
            .build()
    }

    @Bean
    fun ordersDltTopic() = TopicBuilder.name(TopicNames.ORDER + "-dlt").build()


    @Bean
    fun paymentsTopic(): NewTopic {
        return TopicBuilder.name(TopicNames.PAYMENT)
            .partitions(3)
            .replicas(1)
            .build()
    }

    @Bean
    fun paymentsDltTopic() = TopicBuilder.name(TopicNames.PAYMENT + "-dlt").build()

    @Bean
    fun suborderTopic(): NewTopic {
        return TopicBuilder.name(TopicNames.SUBORDER)
            .partitions(3)
            .replicas(1)
            .build()
    }

    @Bean
    fun subordersDltTopic() = TopicBuilder.name(TopicNames.SUBORDER + "-dlt").build()
}