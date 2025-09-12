//package messaging
//
//import com.buoyancy.common.model.dto.messaging.events.OrderEvent
//import com.buoyancy.common.model.enums.OrderStatus
//import com.buoyancy.common.model.enums.TopicNames
//import com.buoyancy.notification.NotificationServiceApplication
//import com.buoyancy.notification.messaging.consumer.OrderListener
//import com.buoyancy.notification.service.MailService
//import org.apache.kafka.clients.consumer.ConsumerRecord
//import org.awaitility.Awaitility.await
//import org.junit.jupiter.api.Test
//import org.mockito.kotlin.*
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.context.MessageSource
//import org.springframework.kafka.core.KafkaTemplate
//import org.springframework.kafka.test.context.EmbeddedKafka
//import org.springframework.test.annotation.DirtiesContext
//import org.springframework.test.context.ActiveProfiles
//import org.springframework.test.context.bean.override.mockito.MockitoBean
//import org.springframework.test.context.bean.override.mockito.MockitoSpyBean
//import java.util.*
//import java.util.concurrent.TimeUnit
//
//@SpringBootTest(classes = [NotificationServiceApplication::class])
//@ActiveProfiles("test")
//@DirtiesContext
//@EmbeddedKafka(partitions = 1, topics = [TopicNames.ORDER],
//    brokerProperties = [ "listeners=PLAINTEXT://localhost:9092", "port=9092" ]
//)
//class OrderListenerIntegrationTest {
//
//    @Autowired
//    private lateinit var kafkaTemplate: KafkaTemplate<String, OrderEvent>
//    @Autowired
//    private lateinit var stringKafkaTemplate: KafkaTemplate<String, String>
//    @MockitoSpyBean
//    private lateinit var orderListener: OrderListener
//    @MockitoBean
//    private lateinit var mailService: MailService
//    @MockitoBean
//    private lateinit var messages: MessageSource
//
//    @Test
//    fun `receiveOrderRecord should send email when event type is CREATED`() {
//        // Given
//        val orderId = UUID.randomUUID()
//        val userId = UUID.randomUUID()
//        val userEmail = "test@example.com"
//        val subject = "Order Subject 1"
//        val body = "Order Created Body 2"
//        val event = OrderEvent(OrderStatus.CREATED, orderId, userId, userEmail)
//
//        whenever(messages.getMessage("notifications.order.created", arrayOf(orderId), Locale.ENGLISH)).thenReturn(body)
//        whenever(messages.getMessage("email.subjects.order", null, Locale.ENGLISH)).thenReturn(subject)
//
//        // When
//        kafkaTemplate.send("orders", event)
//        kafkaTemplate.flush()
//
//        // Then
//        await().atMost(3, TimeUnit.SECONDS).untilAsserted {
//            verify(orderListener, times(1))
//                .receiveOrderRecord(argThat<ConsumerRecord<String, OrderEvent>> { record : ConsumerRecord<String, OrderEvent> ->
//                    record.value()?.let { order -> order.orderId == orderId } ?: false
//                })
//        }
//        verify(messages, timeout(3000)).getMessage("notifications.order.created", arrayOf(orderId), Locale.ENGLISH)
//        verify(messages, timeout(3000)).getMessage("email.subjects.order", null, Locale.ENGLISH)
//        verify(mailService, timeout(3000)).send(userEmail, subject, body)
//    }
//
//    @Test
//    fun `receiveOrderRecord should send email when event type is CLOSED`() {
//        // Given
//        val orderId = UUID.randomUUID()
//        val userId = UUID.randomUUID()
//        val userEmail = "test@example.com"
//        val subject = "Order Subject 2"
//        val body = "Order Created Body 2"
//        val event = OrderEvent(OrderStatus.READY, orderId, userId, userEmail)
//
//        whenever(messages.getMessage("notifications.order.ready", arrayOf(orderId), Locale.ENGLISH)).thenReturn(body)
//        whenever(messages.getMessage("email.subjects.order", null, Locale.ENGLISH)).thenReturn(subject)
//
//        // When
//        kafkaTemplate.send("orders", event)
//        kafkaTemplate.flush()
//
//        // Then
//        await().atMost(3, TimeUnit.SECONDS).untilAsserted {
//            verify(orderListener, times(1))
//                .receiveOrderRecord(argThat<ConsumerRecord<String, OrderEvent>> { record : ConsumerRecord<String, OrderEvent> ->
//                    record.value()?.let { order -> order.orderId == orderId } ?: false
//                })
//        }
//        verify(messages, timeout(3000)).getMessage("notifications.order.ready", arrayOf(orderId), Locale.ENGLISH)
//        verify(messages, timeout(3000)).getMessage("email.subjects.order", null, Locale.ENGLISH)
//        verify(mailService, timeout(3000)).send(userEmail, subject, body)
//    }
//
//    @Test
//    fun `receiveOrderRecord should send email when event type is CANCELLED`() {
//        // Given
//        val orderId = UUID.randomUUID()
//        val userId = UUID.randomUUID()
//        val userEmail = "test@example.com"
//        val event = OrderEvent(OrderStatus.CANCELLED, orderId, userId, userEmail)
//        val subject = "Order Subject 2"
//        val body = "Order Created Body 2"
//
//        whenever(messages.getMessage("notifications.order.cancelled", arrayOf(orderId), Locale.ENGLISH)).thenReturn(body)
//        whenever(messages.getMessage("email.subjects.order", null, Locale.ENGLISH)).thenReturn(subject)
//
//        // When
//        kafkaTemplate.send("orders", event)
//        kafkaTemplate.flush()
//
//        // Then
//        await().atMost(3, TimeUnit.SECONDS).untilAsserted {
//            verify(orderListener, times(1))
//                .receiveOrderRecord(argThat<ConsumerRecord<String, OrderEvent>> { record : ConsumerRecord<String, OrderEvent> ->
//                    record.value()?.let { order -> order.orderId == orderId } ?: false
//                })
//        }
//        verify(messages, timeout(3000)).getMessage("notifications.order.cancelled", arrayOf(orderId), Locale.ENGLISH)
//        verify(messages, timeout(3000)).getMessage("email.subjects.order", null, Locale.ENGLISH)
//        verify(mailService, timeout(3000)).send(userEmail, subject, body)
//    }
//}