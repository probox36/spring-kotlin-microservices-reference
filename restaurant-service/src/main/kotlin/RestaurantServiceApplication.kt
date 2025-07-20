package com.buoyancy.restaurant

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@EntityScan("com.buoyancy.common.model")
@ComponentScan("com.buoyancy.common.model", "com.buoyancy.restaurant")
@SpringBootApplication
class RestaurantServiceApplication

fun main(args: Array<String>) {
    runApplication<RestaurantServiceApplication>(*args)
}