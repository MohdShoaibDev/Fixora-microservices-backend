package com.shoaib.productservice.controller.product.publicController;


import com.shoaib.apiResponse.ApiResponse;
import com.shoaib.apiResponse.PageApiResponse;
import com.shoaib.cart.ClientCartItemRequest;
import com.shoaib.order.OrderProductDtoRequest;
import com.shoaib.productDtos.ProductClientDto;
import com.shoaib.productDtos.ProductIdListDto;
import com.shoaib.productDtos.ReserveProductDto;
import com.shoaib.productDtos.ProductReviewClientDto;
import com.shoaib.productservice.dtos.ProductFilterRequest;
import com.shoaib.productservice.dtos.ProductReviewResponse;
import com.shoaib.productservice.dtos.ProductResponseDto;
import com.shoaib.productservice.entity.Product;
import com.shoaib.productservice.repository.ProductRepository;
import com.shoaib.productservice.service.productReview.ProductReviewService;
import com.shoaib.productservice.service.productService.ProductServiceImpl;
import com.shoaib.productservice.utility.SortDirection;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/public")
public class ProductPublicController {

    private final ProductServiceImpl  productServiceImpl;
    private final ProductReviewService reviewService;

    @GetMapping("/products/{id}")
    public ResponseEntity<ApiResponse<ProductResponseDto>> getProduct(@PathVariable UUID id) {
        return ResponseEntity.ok().body(new ApiResponse<>(true, "Product Details fetch successfully",
                productServiceImpl.getProduct(id)));
    }

    @GetMapping("/products/{productId}/reviews")
    public ResponseEntity<PageApiResponse<List<ProductReviewResponse>>> getProductReviews(
            @PathVariable UUID productId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ProductReviewResponse> reviews = reviewService.getProductReviews(productId, page, size);
        return ResponseEntity.ok(PageApiResponse.<List<ProductReviewResponse>>builder()
                .status(true)
                .message("Reviews fetched successfully")
                .data(reviews.getContent())
                .page(page)
                .totalProducts(reviews.getTotalElements())
                .totalPages(reviews.getTotalPages())
                .build());
    }

    @GetMapping("/products")
    public ResponseEntity<PageApiResponse<List<ProductResponseDto>>> getProducts(@RequestParam(defaultValue = "1") Integer page, @RequestParam(required = false, defaultValue = "")  UUID categoryId,
                                                                                 @RequestParam(required = false, defaultValue = "0.0")  Double minRating, @RequestParam(required = false)  String sortBy) {
        var productFilterRequest = ProductFilterRequest.builder()
                .page(page)
                .categoryId(categoryId)
                .minRating(minRating)
                .build();
        if("asc".equalsIgnoreCase(sortBy)) {
            productFilterRequest.setSortBy(SortDirection.ASC);
        } else if ("desc".equalsIgnoreCase(sortBy)) {
            productFilterRequest.setSortBy(SortDirection.DESC);
        }else{
            productFilterRequest.setSortBy(null);
        }
        var pageResponseDto = productServiceImpl.getProducts(productFilterRequest);
        var pageApiResponse = PageApiResponse.<List<ProductResponseDto>>builder()
                .page(pageResponseDto.getPage() + 1)
                .totalPages(pageResponseDto.getTotalPages())
                .totalProducts(pageResponseDto.getTotalElements())
                .message("Products fetch successfully")
                .status(true)
                .data(pageResponseDto.getData())
                .build();

        return ResponseEntity.ok(pageApiResponse);
    }

    @GetMapping("products/general-service-booking/{id}")
    public ResponseEntity<ApiResponse<ProductClientDto>> getGeneralInspectionProductsDetailsByCategoryId(@PathVariable("id") UUID categoryId) {
        System.out.println("getGeneralInspectionProductsDetailsByCategoryId: " + categoryId);
        return ResponseEntity.ok().body(new ApiResponse<>(true, "Product fetch successfully", productServiceImpl.getGeneralInspectionProductsDetailsByCategoryId(categoryId)));
    }

    @PostMapping("/internal/products")
    public List<ProductClientDto> getProductsDetails(@RequestBody ProductIdListDto productIdListDto) {
        return productServiceImpl.getProductsDetails(productIdListDto);
    }

    @PostMapping("/internal/reserve-products")
    public HashMap<UUID, ReserveProductDto> getProductsPrice(@RequestBody OrderProductDtoRequest<List<ClientCartItemRequest>> orderProductDtoRequest) {
        return productServiceImpl.getProductsPrice(orderProductDtoRequest);
    }

    @PostMapping("/internal/product-reviews")
    public Map<UUID, ProductReviewClientDto> getUserProductReviews(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestBody ProductIdListDto productIdListDto) {
        return reviewService.getUserProductReviews(userId, productIdListDto.getListId());
    }

}
