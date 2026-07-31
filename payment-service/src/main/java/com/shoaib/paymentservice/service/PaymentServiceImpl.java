package com.shoaib.paymentservice.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import com.shoaib.kafka.dtos.PaymentSuccessKafkaEvent;
import com.shoaib.kafka.dtos.KafkaEnvelope;
import com.shoaib.kafka.util.KafkaEventType;
import com.shoaib.kafka.util.KafkaTopics;
import com.shoaib.payment.RazorpayOrderIdRequestDto;
import com.shoaib.payment.RazorpayOrderIdResponse;
import com.shoaib.paymentservice.dtos.VerifyRazorpayPaymentRequest;
import com.shoaib.paymentservice.dtos.VerifyRazorpayPaymentResponse;
import com.shoaib.paymentservice.entity.Payment;
import com.shoaib.paymentservice.exception.RazorpayOrderIdCreationException;
import com.shoaib.paymentservice.kafka.KafkaProducer;
import com.shoaib.paymentservice.razorpay.RazorpayConfigurationProperties;
import com.shoaib.paymentservice.repository.PaymentRepository;
import com.shoaib.paymentservice.util.enums.PaymentMethod;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final RazorpayClient razorpayClient;
    private final PaymentRepository paymentRepository;
    private final RazorpayConfigurationProperties  razorpayConfigurationProperties;
    private final KafkaProducer  kafkaProducer;


    @Override
    public RazorpayOrderIdResponse createRazorpayOrder(UUID userId, RazorpayOrderIdRequestDto razorpayOrderRequest) {
        System.out.println("API: " + System.getenv("RAZORPAY_API_KEY"));
        System.out.println("SECRET: " + System.getenv("RAZORPAY_SECRET_KEY"));
        Payment payment = Payment.create(userId, razorpayOrderRequest.getBookingId(), razorpayOrderRequest.getAmount(),
                razorpayOrderRequest.getCurrency());
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", razorpayOrderRequest.getAmount().multiply(BigDecimal.valueOf(100)).intValue());
        orderRequest.put("currency", razorpayOrderRequest.getCurrency());
        orderRequest.put("receipt", razorpayOrderRequest.getBookingId());
        try {
            Order order = razorpayClient.orders.create(orderRequest);
            payment.assignGatewayOrderId(order.get("id"));
            paymentRepository.save(payment);
            return new RazorpayOrderIdResponse(payment.getGatewayOrderId(), payment.getAmount(),
                    payment.getCurrency(), razorpayConfigurationProperties.keyId(), payment.getCreatedAt());
        } catch (RazorpayException e) {
            throw new RazorpayOrderIdCreationException(e.getMessage());
        }
    }

    @Override
    public VerifyRazorpayPaymentResponse verifyRazorpayPayment(UUID userId, VerifyRazorpayPaymentRequest verifyRazorpayPaymentRequest) {
        Payment payment = paymentRepository.findByGatewayOrderId(verifyRazorpayPaymentRequest.getRazorpayOrderId()).orElseThrow(() ->
                new RuntimeException("Payment verification failed"));
        if(!payment.getUserId().equals(userId)){
            throw new RuntimeException("Payment does not belong to this user");
        }
        verifyRazorpayPaymentRequest.setRazorpayOrderId(payment.getGatewayOrderId());
        boolean paymentIsAuthenticate = razorpayPaymentVerification(verifyRazorpayPaymentRequest);
        if (!paymentIsAuthenticate) {
            payment.markFailed("500", "Payment authentication failed");
            paymentRepository.save(payment);
            throw new RuntimeException("Payment verification failed");
        }
        payment.markSuccessful(verifyRazorpayPaymentRequest.getRazorpayPaymentId(),
                verifyRazorpayPaymentRequest.getRazorpaySignature(), PaymentMethod.CARD);
        paymentRepository.save(payment);
        PaymentSuccessKafkaEvent paymentSuccessKafkaEvent = new PaymentSuccessKafkaEvent( payment.getId(), payment.getGatewayOrderId(),
                payment.getGatewayPaymentId(),
                userId);
        kafkaProducer.send(KafkaTopics.PAYMENT, payment.getId().toString(),
                new KafkaEnvelope<>(KafkaEventType.PAYMENT_SUCCESS,  paymentSuccessKafkaEvent));
        return VerifyRazorpayPaymentResponse.builder()
                .paymentId(payment.getId())
                .build();
    }

    private boolean razorpayPaymentVerification(VerifyRazorpayPaymentRequest verifyRazorpayPaymentRequest) {
        JSONObject options = new JSONObject();
        options.put("razorpay_order_id", verifyRazorpayPaymentRequest.getRazorpayOrderId());
        options.put("razorpay_payment_id", verifyRazorpayPaymentRequest.getRazorpayPaymentId());
        options.put("razorpay_signature", verifyRazorpayPaymentRequest.getRazorpaySignature());
        try{
            return Utils.verifyPaymentSignature(options, razorpayConfigurationProperties.keySecret());
        }catch(RazorpayException e){
            throw new RuntimeException(e.getMessage());
        }
    }
}
