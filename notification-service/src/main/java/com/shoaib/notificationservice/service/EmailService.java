package com.shoaib.notificationservice.service;

public interface EmailService {

    void sendTextEmail(
            String to,
            String subject,
            String body
    );
}
