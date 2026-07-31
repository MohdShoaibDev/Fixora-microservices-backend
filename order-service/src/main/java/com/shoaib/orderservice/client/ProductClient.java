package com.shoaib.orderservice.client;

import com.shoaib.cart.ClientCartItemRequest;
import com.shoaib.order.OrderProductDtoRequest;
import com.shoaib.productDtos.ReserveProductDto;
import com.shoaib.productDtos.ProductIdListDto;
import com.shoaib.productDtos.ProductReviewClientDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "product-client",
url = "${services.product-service.url}")
public interface ProductClient {

    @PostMapping("/public/internal/reserve-products")
    HashMap<UUID, ReserveProductDto> getProductsPrice(@RequestBody OrderProductDtoRequest<List<ClientCartItemRequest>> orderProductDtoRequest,
                                                    @RequestHeader UUID userId);

    @PostMapping("/public/internal/product-reviews")
    Map<UUID, ProductReviewClientDto> getUserProductReviews(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestBody ProductIdListDto productIdListDto);
}
