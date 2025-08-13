package com.buoyancy.notification.service

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

@Service
class MailService {

    private val log = KotlinLogging.logger {}

    @Autowired
    private lateinit var mailSender: JavaMailSender

    fun send(to: String, subject: String, body: String) {
        log.info { "Sending email with subject $subject to $to" }
        val message = SimpleMailMessage()
        // TODO: Убрать
        message.setTo(to)
        message.subject = subject
        message.text = body

        mailSender.send(message)
    }
}