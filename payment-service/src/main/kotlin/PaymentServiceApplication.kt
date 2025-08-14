package com.buoyancy.payment

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.scheduling.annotation.EnableScheduling

@EnableJpaRepositories("com.buoyancy.common.repository")
@EntityScan("com.buoyancy.common.model")
@ComponentScan("com.buoyancy.common.model", "com.buoyancy.common.exceptions", "com.buoyancy.payment", "com.buoyancy.common")
@SpringBootApplication
@EnableCaching
@EnableScheduling
class PaymentServiceApplication

fun main(args: Array<String>) {
    runApplication<PaymentServiceApplication>(*args)
}