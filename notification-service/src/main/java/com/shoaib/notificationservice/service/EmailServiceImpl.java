package com.shoaib.notificationservice.service;

import com.shoaib.notificationservice.exception.EmailSendingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${app.mail.from}")
    private String fromEmail;

    @Override
    public void sendTextEmail(
            String to,
            String subject,
            String body
    ) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            javaMailSender.send(message);

        } catch (MailException exception) {
            log.error(
                    "Failed to send text email to {}",
                    to,
                    exception
            );

            throw new EmailSendingException(
                    "Failed to send email",
                    exception
            );
        }
    }
}
