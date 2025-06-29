package com.buoyancy.notification.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

@Service
class MailService {

    @Autowired
    private lateinit var mailSender: JavaMailSender

    fun send(to: String, subject: String, body: String) {
        val message = SimpleMailMessage()
        message.setTo(to)
        message.subject = subject
        message.text = body

        mailSender.send(message)
    }
}