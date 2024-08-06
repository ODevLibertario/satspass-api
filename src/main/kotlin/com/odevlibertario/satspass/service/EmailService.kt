package com.odevlibertario.satspass.service

import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

@Service
class EmailService(private val mailSender: JavaMailSender) {

    fun sendSimpleEmail(to: String, subject: String, text: String) {
        val message = SimpleMailMessage()
        message.from = "satspass@trial-3zxk54v5z06ljy6v.mlsender.net"
        message.setTo(to)
        message.setSubject(subject)
        message.setText(text)
        mailSender.send(message)
    }
}