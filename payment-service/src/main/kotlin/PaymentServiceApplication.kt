package com.buoyancy.payment

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.scheduling.annotation.EnableScheduling

@EntityScan("com.buoyancy.common.model")
@ComponentScan("com.buoyancy.common.model", "com.buoyancy.restaurant")
@SpringBootApplication
@EnableScheduling
class PaymentServiceApplication

fun main(args: Array<String>) {
    runApplication<PaymentServiceApplication>(*args)
}