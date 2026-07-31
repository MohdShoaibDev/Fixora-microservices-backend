package com.shoaib.paymentservice.razorpay;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({RazorpayConfigurationProperties.class})
public class RazorpayConfig {

    @Bean
    RazorpayClient razorpayClient(RazorpayConfigurationProperties razorpayConfigurationProperties) throws RazorpayException {
        return new RazorpayClient(razorpayConfigurationProperties.keyId(),razorpayConfigurationProperties.keySecret());
    }
}
