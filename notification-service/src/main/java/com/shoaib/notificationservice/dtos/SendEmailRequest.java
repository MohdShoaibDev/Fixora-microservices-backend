package com.shoaib.notificationservice.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendEmailRequest{

        @NotBlank(message = "Recipient email is required")
        @Email(message = "Recipient email is invalid")
        String to;

        @NotBlank(message = "Subject is required")
        String subject;

        @NotBlank(message = "Email body is required")
        String body;
}
