package com.buoyancy.restaurant

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@EnableJpaRepositories("com.buoyancy.common.repository")
@EntityScan("com.buoyancy.common.model")
@ComponentScan("com.buoyancy.common", "com.buoyancy.restaurant")
@EnableCaching
@EnableDiscoveryClient
@SpringBootApplication
class RestaurantServiceApplication

fun main(args: Array<String>) {
    runApplication<RestaurantServiceApplication>(*args)
}