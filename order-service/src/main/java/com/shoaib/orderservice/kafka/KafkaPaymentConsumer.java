package com.shoaib.orderservice.kafka;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import com.shoaib.kafka.dtos.PaymentSuccessKafkaEvent;
import com.shoaib.kafka.dtos.KafkaEnvelope;
import com.shoaib.kafka.util.KafkaEventType;
import com.shoaib.kafka.util.KafkaGroups;
import com.shoaib.kafka.util.KafkaTopics;
import com.shoaib.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaPaymentConsumer {

    private final ObjectMapper objectMapper;
    private final OrderService orderService;

    @KafkaListener(topics = KafkaTopics.PAYMENT,
    groupId = KafkaGroups.ORDER_GROUP)
    public void orderPaymentConsumer(String message) throws JacksonException {

        JavaType envelopeType = objectMapper.getTypeFactory().constructParametricType(
                KafkaEnvelope.class, JsonNode.class
        );

        KafkaEnvelope<JsonNode> jsonNode =  objectMapper.readValue(message,envelopeType);

        if (jsonNode.eventType().equals(KafkaEventType.PAYMENT_SUCCESS)) {
            orderService.orderPaymentCompleted(objectMapper.treeToValue(jsonNode.data(),PaymentSuccessKafkaEvent.class));
        }

    }

}
