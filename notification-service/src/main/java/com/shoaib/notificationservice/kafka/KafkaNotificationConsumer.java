package com.shoaib.notificationservice.kafka;

import com.shoaib.kafka.dtos.KafkaEnvelope;
import com.shoaib.kafka.dtos.RegisterRequest;
import com.shoaib.kafka.util.KafkaEventType;
import com.shoaib.kafka.util.KafkaGroups;
import com.shoaib.kafka.util.KafkaTopics;
import com.shoaib.notificationservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaNotificationConsumer {

    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = KafkaTopics.USER,
            groupId = KafkaGroups.NOTIFICATION_GROUP
    )
    public void orderPaymentCompleted(String message) {
        try{
            JavaType envelopeType = objectMapper.getTypeFactory()
                    .constructParametricType(
                            KafkaEnvelope.class,
                            JsonNode.class
                    );

            KafkaEnvelope<JsonNode> envelope =
                    objectMapper.readValue(message, envelopeType);

            if (KafkaEventType.USER_ONBOARDING.equals(envelope.eventType())) {
                RegisterRequest event =
                        objectMapper.treeToValue(
                                envelope.data(),
                                RegisterRequest.class
                        );
                emailService.sendTextEmail(event.getEmail(),"Verify Your Fixora Account", """
Hello,

Your Fixora verification code is: %s

This OTP is valid for 5 minutes.

If you didn't request this, please ignore this email.

Thanks,
The Fixora Team
""".formatted(event.getOtp()));

            }
        }catch (JacksonException e){
            log.error("shoaib kafka 3 error {}", e.getMessage());
        }
    }
}