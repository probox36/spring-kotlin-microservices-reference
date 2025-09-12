package com.buoyancy.notification.service

import com.buoyancy.common.model.entity.Product
import com.buoyancy.common.model.enums.CacheNames
import com.buoyancy.common.model.interfaces.OrderDetails
import com.buoyancy.common.repository.SuborderRepository
import com.buoyancy.common.utils.find
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.Cacheable
import org.springframework.context.MessageSource
import org.springframework.stereotype.Service
import java.util.*

@Service
class NotificationService {

    @Autowired
    private lateinit var repo: SuborderRepository
    @Autowired
    private lateinit var messages : MessageSource
    @Autowired
    private lateinit var email: MailService

    @Cacheable(CacheNames.SUBORDERS)
    private fun getSubordersByOrderId(orderId: UUID) = repo.findByOrderId(orderId)
    private fun getItemListString(items: List<Product>): String = items.joinToString(",\n") { it.name }

    @Transactional
    fun notifyRestaurants(orderId: UUID) {
        val suborders = getSubordersByOrderId(orderId)
        suborders.forEach {
            val body = messages.find("notifications.restaurant.suborder.available",
                orderId, it.items.size, getItemListString(it.items), it.items.sumOf { it.price })

            email.send(
                to = it.restaurant.email,
                subject = messages.find("email.subjects.suborder"),
                body = body
            )
        }
    }

    fun notifyUser(event: OrderDetails, message: String) {
        email.send(
            to = event.userEmail,
            subject = messages.find("email.subjects.payment"),
            body = message
        )
    }
}