package com.shoaib.kafka.util;

public class KafkaTopics {
    private KafkaTopics() {}

    public static final String ORDER = "fixora.order.created";
    public static final String PAYMENT = "fixora.payment.completed";
    public static final String USER = "fixora.user";
}
