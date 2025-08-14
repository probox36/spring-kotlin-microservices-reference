package com.buoyancy.order

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@EnableJpaRepositories("com.buoyancy.common.repository")
@EntityScan("com.buoyancy.common.model")
@EnableCaching
@ComponentScan("com.buoyancy.common.model", "com.buoyancy.common.exceptions", "com.buoyancy.order", "com.buoyancy.common")
@SpringBootApplication
class OrderServiceApplication

fun main(args: Array<String>) {
    runApplication<OrderServiceApplication>(*args)
}