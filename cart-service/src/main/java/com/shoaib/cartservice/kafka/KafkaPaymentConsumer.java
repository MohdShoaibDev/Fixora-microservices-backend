package com.shoaib.cartservice.kafka;

import com.shoaib.cartservice.service.serviceInterface.CartService;
import com.shoaib.kafka.dtos.PaymentSuccessKafkaEvent;
import com.shoaib.kafka.dtos.KafkaEnvelope;
import com.shoaib.kafka.util.KafkaEventType;
import com.shoaib.kafka.util.KafkaGroups;
import com.shoaib.kafka.util.KafkaTopics;
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
public class KafkaPaymentConsumer {

    private final ObjectMapper objectMapper;
    private final CartService cartService;

    @KafkaListener(
            topics = KafkaTopics.PAYMENT,
            groupId = KafkaGroups.CART_GROUP
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

            if (KafkaEventType.PAYMENT_SUCCESS.equals(envelope.eventType())) {
                PaymentSuccessKafkaEvent event =
                        objectMapper.treeToValue(
                                envelope.data(),
                                PaymentSuccessKafkaEvent.class
                        );

                cartService.clearCart(event.userId());
            }
        }catch (JacksonException e){
            log.error("shoaib kafka 3 error {}", e.getMessage());
        }
    }
}