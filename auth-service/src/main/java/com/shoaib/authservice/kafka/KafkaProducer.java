package com.shoaib.authservice.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public <T> void send(String kafkaTopic, String key, T obj){
        kafkaTemplate.send(kafkaTopic, key, obj);
    }

}
