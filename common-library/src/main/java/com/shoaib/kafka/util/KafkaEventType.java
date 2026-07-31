package com.shoaib.kafka.util;

public final class KafkaEventType {
    private KafkaEventType() {}

    public static final String PAYMENT_SUCCESS = "fixora.payment.success";
    public static final String PAYMENT_FAILED = "fixora.payment.failed";
    public static final String REGISTER_REQUEST = "fixora.register.request";

    public static final String USER_REGISTER = "fixora.payment.success";
    public static final String USER_ONBOARDING = "fixora.register.request";
}
