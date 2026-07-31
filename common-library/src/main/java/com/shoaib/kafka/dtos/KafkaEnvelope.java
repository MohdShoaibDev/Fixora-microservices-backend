package com.shoaib.kafka.dtos;


public record KafkaEnvelope<T> (
        String eventType,
        T data
){
}
