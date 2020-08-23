package com.md.service.email

import com.md.dto.EmailDto
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*
import javax.mail.Message
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

@Component
class EmailService {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(EmailService::class.java)
    }

    @Value("\${emailService.EMAIL}")
    private val EMAIL: String? = null

    @Value("\${emailService.PASSWORD}")
    private val PASSWORD: String? = null

    fun sendEmail(studentEmail: String, emailDto: EmailDto) {
        try {
            val host = "smtp.gmail.com"
            val from = EMAIL
            val password = PASSWORD

            val properties = Properties()

            with(properties) {
                put("mail.smtp.starttls.enable", "false");
                put("mail.smtp.host", host);
                put("mail.smtp.user", from);
                put("mail.smtp.password", password);
                put("mail.smtp.port", "587")
                put("mail.smtp.auth", "true")
            }

            val session = Session.getDefaultInstance(properties, null)
            val message = MimeMessage(session)
            message.setFrom(InternetAddress(from))

            message.setRecipients(Message.RecipientType.TO, studentEmail)
            message.subject = emailDto.subject
            message.setText(emailDto.message)

            val transport = session.getTransport("smtps")
            transport.connect(host, from, password)
            transport.sendMessage(message, message.allRecipients)
            transport.close()
            LOGGER.info("message sent to the student {}", studentEmail)
        }
        catch (ex: Exception) {
            throw ex
        }
    }
}
