package com.shoaib.orderservice.controller;

import com.shoaib.apiResponse.ApiResponse;
import com.shoaib.apiResponse.PageApiResponse;
import com.shoaib.orderservice.dtos.OrderDto;
import com.shoaib.orderservice.dtos.OrderIdDto;
import com.shoaib.orderservice.dtos.OrderPageDto;
import com.shoaib.orderservice.dtos.OrderRequestDto;
import com.shoaib.orderservice.service.OrderService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("orders")
public class OrderController {

    private final OrderService orderServiceImpl;

    @GetMapping("get-orders")
    ResponseEntity<PageApiResponse<List<OrderDto>>> getOrders(@RequestHeader("X-User-Id") UUID userId,
                                                        @RequestParam(defaultValue = "1")
                                                        @Min(value = 1, message = "Page must be greater than or equal to 1")
                                                        Integer page) {
        OrderPageDto<List<OrderDto>> orders = orderServiceImpl.getOrders(userId, page);
        return ResponseEntity.ok().body(
                new PageApiResponse<>(true, "Orders fetch successfully",orders.getData(),
                        orders.getPage(),
                        orders.getTotalElements(),
                        orders.getTotalPages()
                )
        );
    }

    @PostMapping("/create-order")
    public ResponseEntity<ApiResponse<OrderIdDto>> createOrderId(@RequestHeader("X-User-Id") UUID userId,
                                                                 @RequestHeader("Authorization") String token,
                                                                 @RequestBody OrderRequestDto orderRequestDto) {
        return ResponseEntity.ok(new ApiResponse<>(true, "OrderId created successfully",
                orderServiceImpl.createOrderId(userId, token, orderRequestDto)));
    }

    @PostMapping("/mark-payment-failed")
    public ResponseEntity<ApiResponse<Object>> markPaymentFailed(@RequestHeader("Razorpay-Order-Id") String orderID){
        orderServiceImpl.markPaymentFailed(orderID);
        return ResponseEntity.ok(new ApiResponse<>(true, "Order status updated successfully",
                null));
    }

}
