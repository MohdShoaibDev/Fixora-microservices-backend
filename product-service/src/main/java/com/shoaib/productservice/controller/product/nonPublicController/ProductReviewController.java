package com.shoaib.productservice.controller.product.nonPublicController;

import com.shoaib.apiResponse.ApiResponse;
import com.shoaib.apiResponse.PageApiResponse;
import com.shoaib.productservice.dtos.CreateProductReviewRequest;
import com.shoaib.productservice.dtos.ProductReviewResponse;
import com.shoaib.productservice.dtos.UpdateProductReviewRequest;
import com.shoaib.productservice.service.productReview.ProductReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductReviewController {
    private final ProductReviewService reviewService;

    @PostMapping("/{productId}/reviews")
    public ResponseEntity<ApiResponse<ProductReviewResponse>> createReview(
            @PathVariable UUID productId, @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader("X-User-Name") String userName,
            @Valid @RequestBody CreateProductReviewRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true,
                "Review created successfully", reviewService.createReview(productId, userId, userName, request)));
    }

    @PutMapping("/reviews/{reviewId}")
    public ResponseEntity<ApiResponse<ProductReviewResponse>> updateReview(
            @PathVariable UUID reviewId, @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody UpdateProductReviewRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Review updated successfully",
                reviewService.updateReview(reviewId, userId, request)));
    }

    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<ApiResponse<Object>> deleteReview(
            @PathVariable UUID reviewId, @RequestHeader("X-User-Id") UUID userId) {
        reviewService.deleteReview(reviewId, userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Review deleted successfully", null));
    }

    @GetMapping("/{productId}/reviews/me")
    public ResponseEntity<PageApiResponse<List<ProductReviewResponse>>> getMyReview(
            @PathVariable UUID productId,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ProductReviewResponse> reviews = reviewService.getMyReview(productId, userId, page, size);
        return ResponseEntity.ok(PageApiResponse.<List<ProductReviewResponse>>builder()
                .status(true).message("Review fetched successfully").data(reviews.getContent())
                .page(page).totalProducts(reviews.getTotalElements()).totalPages(reviews.getTotalPages()).build());
    }
}
