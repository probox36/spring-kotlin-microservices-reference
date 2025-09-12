//package service
//
//import com.buoyancy.notification.NotificationServiceApplication
//import com.buoyancy.notification.service.MailService
//import org.junit.jupiter.api.Test
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.beans.factory.annotation.Value
//import org.springframework.boot.test.context.SpringBootTest
//
//@SpringBootTest(classes = [NotificationServiceApplication::class])
//class MailServiceTest {
//
//    @Autowired
//    private lateinit var notificationService: MailService
//    @Value("\${spring.mail.username}")
//    private lateinit var username: String
//
//    @Test
//    fun testSend() {
//        notificationService.send(
//            to = username,
//            subject = "Test Email",
//            body = "This is a test email from NotificationServiceTest"
//        )
//    }
//}