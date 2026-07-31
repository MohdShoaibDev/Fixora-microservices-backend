package com.shoaib.orderservice.service;

import com.shoaib.cart.ClientCartItemRequest;
import com.shoaib.kafka.dtos.PaymentSuccessKafkaEvent;
import com.shoaib.order.OrderProductDtoRequest;
import com.shoaib.orderservice.client.CartClient;
import com.shoaib.orderservice.client.PaymentClient;
import com.shoaib.orderservice.client.ProductClient;
import com.shoaib.orderservice.dtos.OrderDto;
import com.shoaib.orderservice.dtos.OrderIdDto;
import com.shoaib.orderservice.dtos.OrderPageDto;
import com.shoaib.orderservice.dtos.OrderRequestDto;
import com.shoaib.orderservice.entity.Order;
import com.shoaib.orderservice.entity.OrderItem;
import com.shoaib.orderservice.mapper.Mapper;
import com.shoaib.orderservice.repository.OrderItemRepository;
import com.shoaib.orderservice.repository.OrderRepository;
import com.shoaib.orderservice.util.enums.OrderStatus;
import com.shoaib.orderservice.util.enums.PaymentStatus;
import com.shoaib.payment.RazorpayOrderIdRequestDto;
import com.shoaib.payment.RazorpayOrderIdResponse;
import com.shoaib.productDtos.ReserveProductDto;
import com.shoaib.productDtos.ProductIdListDto;
import com.shoaib.productDtos.ProductReviewClientDto;
import com.shoaib.util.enums.Currency;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartClient  cartClient;
    private final ProductClient productClient;
    private final PaymentClient  paymentClient;

    @Override
    public OrderPageDto<List<OrderDto>> getOrders(UUID userId, Integer requestPage) {
        Pageable page = PageRequest.of(requestPage - 1, 10, Sort.by(Sort.Direction.DESC,
                "createdAt"));
        Page<Order> pageRequest = orderRepository.findByUserId(userId, page);
        List<OrderDto> orderDtoList = new ArrayList<>();
        for(Order order:  pageRequest.getContent()) {
            List<OrderItem> orderItemList = orderItemRepository.findByOrderId(order.getId());
            OrderDto orderDto = Mapper.mapOrderIntoOrderDto(order,orderItemList);
            orderDtoList.add(orderDto);
        }
        List<UUID> productIds = orderDtoList.stream()
                .flatMap(order -> order.getOrderItemList().stream())
                .map(orderItem -> orderItem.getProductId())
                .distinct()
                .toList();
        if (!productIds.isEmpty()) {
            Map<UUID, ProductReviewClientDto> reviews = productClient.getUserProductReviews(userId,
                    ProductIdListDto.builder().listId(productIds).build());
            orderDtoList.stream()
                    .flatMap(order -> order.getOrderItemList().stream())
                    .forEach(orderItem -> {
                        ProductReviewClientDto review = reviews.get(orderItem.getProductId());
                        if (review != null) {
                            orderItem.setReviewed(true);
                            orderItem.setReviewId(review.getReviewId());
                            orderItem.setReview(review.getReview());
                            orderItem.setComment(review.getComment());
                        }
                    });
        }
        return OrderPageDto.<List<OrderDto>>builder()
                .data(orderDtoList)
                .totalPages(pageRequest.getTotalPages())
                .totalElements(pageRequest.getTotalElements())
                .page(pageRequest.getNumber())
                .build();
    }

    @Transactional
    @Override
    public OrderIdDto createOrderId(UUID userId, String token, OrderRequestDto orderRequestDto) {
        List<ClientCartItemRequest> clientCartItemRequestList = cartClient.getCartItem(userId);
        if(clientCartItemRequestList.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }
        Order order = Order.create(userId, Currency.INR, orderRequestDto.addressId());
        HashMap<UUID, ReserveProductDto> map = productClient.getProductsPrice(
                OrderProductDtoRequest.<List<ClientCartItemRequest>>builder()
                        .orderId(order.getId())
                        .data(clientCartItemRequestList)
                        .build()
                ,
                userId
        );
        if(map.isEmpty()){
            order.setOrderStatus(OrderStatus.CANCELLED);
            order.setPaymentStatus(PaymentStatus.CANCELLED);
            orderRepository.save(order);
            throw new RuntimeException("Your requested quantity exceeds the available stock");
        }
        order.setPaymentStatus(PaymentStatus.PENDING);
        List<OrderItem> orderItemList = new ArrayList<>();
        orderRepository.save(order);
        map.forEach((productId, reserveProductDto) -> {
            orderItemList.add(
                    OrderItem.builder()
                            .orderId(order.getId())
                            .productId(productId)
                            .price(reserveProductDto.getPrice())
                            .productName(reserveProductDto.getProductName())
                            .productImage(reserveProductDto.getProductImage())
                            .productDescription(reserveProductDto.getProductDescription())
                            .build()
            );
        });
        orderItemRepository.saveAll(orderItemList);
        BigDecimal amount = clientCartItemRequestList.stream().map( clientCartItemRequest ->
                map.get(clientCartItemRequest.getProductId()).getPrice()).reduce(BigDecimal.ZERO, BigDecimal::add);
        RazorpayOrderIdRequestDto  razorpayOrderIdRequestDto = RazorpayOrderIdRequestDto.builder()
                        .currency(Currency.INR)
                                .bookingId(order.getId())
                                        .amount(amount)
                                                .build();

        RazorpayOrderIdResponse razorpayOrderIdResponse = paymentClient.createOrderId(userId, token, razorpayOrderIdRequestDto);

        if(razorpayOrderIdResponse.getGatewayOrderId() == null) {
            throw new RuntimeException("Something went wrong with payment, try again");
        }
        order.setRazorpayOrderId(razorpayOrderIdResponse.getGatewayOrderId());
        orderRepository.save(order);

        return OrderIdDto.builder()
                .id(order.getId())
                .amount(amount)
                .currency(Currency.INR)
                .orderId(razorpayOrderIdResponse.getGatewayOrderId())
                .keyId(razorpayOrderIdResponse.getKeyId())
                .build();
    }

    @Transactional
    public void orderPaymentCompleted(PaymentSuccessKafkaEvent paymentSuccessKafkaEvent) {
        Optional<Order> orderIsPresent = orderRepository.findByRazorpayOrderId(paymentSuccessKafkaEvent.razorpayOrderId());
        if(orderIsPresent.isPresent()){
            Order order = orderIsPresent.get();
            order.setPaymentStatus(PaymentStatus.PAID);
            order.setRazorpayPaymentId(paymentSuccessKafkaEvent.paymentId());
            order.setOrderStatus(paymentSuccessKafkaEvent.gatewayPaymentId() != null ? OrderStatus.COMPLETED : OrderStatus.FAILED);
            orderRepository.save(order);
        }
    }

    @Transactional
    public void markPaymentFailed(String orderID){
        orderRepository.findByRazorpayOrderIdAndUpdatePaymentAndOrderStatus(PaymentStatus.FAILED.name(),OrderStatus.FAILED.name(), orderID);
    }
}
