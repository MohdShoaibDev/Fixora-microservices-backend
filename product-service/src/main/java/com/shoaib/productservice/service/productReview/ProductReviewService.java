package com.shoaib.productservice.service.productReview;

import com.shoaib.productservice.dtos.CreateProductReviewRequest;
import com.shoaib.productservice.dtos.ProductReviewResponse;
import com.shoaib.productservice.dtos.UpdateProductReviewRequest;
import com.shoaib.productDtos.ProductReviewClientDto;
import org.springframework.data.domain.Page;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public interface ProductReviewService {
    ProductReviewResponse createReview(UUID productId, UUID userId, String userName,
                                       CreateProductReviewRequest request);
    ProductReviewResponse updateReview(UUID reviewId, UUID userId, UpdateProductReviewRequest request);
    void deleteReview(UUID reviewId, UUID userId);
    Page<ProductReviewResponse> getProductReviews(UUID productId, int page, int size);
    Page<ProductReviewResponse> getMyReview(UUID productId, UUID userId, int page, int size);
    Map<UUID, ProductReviewClientDto> getUserProductReviews(UUID userId, Collection<UUID> productIds);
}
