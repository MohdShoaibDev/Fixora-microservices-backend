package com.shoaib.productservice.service.productReview;

import com.shoaib.productservice.dtos.CreateProductReviewRequest;
import com.shoaib.productservice.dtos.ProductReviewResponse;
import com.shoaib.productservice.dtos.UpdateProductReviewRequest;
import com.shoaib.productDtos.ProductReviewClientDto;
import com.shoaib.productservice.entity.Product;
import com.shoaib.productservice.entity.ProductReview;
import com.shoaib.productservice.exception.DuplicateProductReviewException;
import com.shoaib.productservice.exception.InvalidPaginationException;
import com.shoaib.productservice.exception.ProductInactiveException;
import com.shoaib.productservice.exception.ProductNotFoundException;
import com.shoaib.productservice.exception.ProductReviewNotFoundException;
import com.shoaib.productservice.repository.ProductRepository;
import com.shoaib.productservice.repository.ProductReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductReviewServiceImpl implements ProductReviewService {
    private final ProductReviewRepository reviewRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public ProductReviewResponse createReview(UUID productId, UUID userId, String userName,
                                              CreateProductReviewRequest request) {
        if (userName == null || userName.isBlank() || userName.trim().length() > 150) {
            throw new IllegalArgumentException("User name must contain between 1 and 150 characters");
        }
        Product product = lockProduct(productId);
        if (!Boolean.TRUE.equals(product.getActive())) throw new ProductInactiveException();
        if (reviewRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new DuplicateProductReviewException();
        }
        ProductReview review = ProductReview.create(productId, userId, userName, request.rating(), request.comment());
        try {
            reviewRepository.saveAndFlush(review);
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicateProductReviewException(exception);
        }
        product.addReviewRating(request.rating());
        return toResponse(review);
    }

    @Override
    @Transactional
    public ProductReviewResponse updateReview(UUID reviewId, UUID userId, UpdateProductReviewRequest request) {
        ProductReview review = ownedReview(reviewId, userId);
        int oldRating = review.getRating();
        Product product = lockProduct(review.getProductId());
        review.update(request.rating(), request.comment());
        product.updateReviewRating(oldRating, request.rating());
        return toResponse(review);
    }

    @Override
    @Transactional
    public void deleteReview(UUID reviewId, UUID userId) {
        ProductReview review = ownedReview(reviewId, userId);
        Product product = lockProduct(review.getProductId());
        int deletedRating = review.getRating();
        reviewRepository.delete(review);
        product.removeReviewRating(deletedRating);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductReviewResponse> getProductReviews(UUID productId, int page, int size) {
        validatePagination(page, size);
        if (!productRepository.existsById(productId)) throw new ProductNotFoundException();
        return reviewRepository.findByProductId(productId,
                        PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt")))
                .map(ProductReviewServiceImpl::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductReviewResponse> getMyReview(UUID productId, UUID userId, int page, int size) {
        validatePagination(page, size);
        if (!productRepository.existsById(productId)) throw new ProductNotFoundException();
        return reviewRepository.findByUserIdAndProductId(userId, productId,
                        PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt")))
                .map(ProductReviewServiceImpl::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<UUID, ProductReviewClientDto> getUserProductReviews(UUID userId, Collection<UUID> productIds) {
        if (productIds == null || productIds.isEmpty()) return Map.of();
        return reviewRepository.findByUserIdAndProductIdIn(userId, productIds).stream()
                .collect(Collectors.toMap(ProductReview::getProductId, review -> ProductReviewClientDto.builder()
                        .reviewId(review.getId())
                        .isReviewed(true)
                        .review(review.getRating())
                        .comment(review.getComment())
                        .build()));
    }

    private Product lockProduct(UUID productId) {
        return productRepository.findByIdForReviewUpdate(productId).orElseThrow(ProductNotFoundException::new);
    }

    private ProductReview ownedReview(UUID reviewId, UUID userId) {
        return reviewRepository.findByIdAndUserId(reviewId, userId)
                .orElseThrow(ProductReviewNotFoundException::new);
    }

    private static void validatePagination(int page, int size) {
        if (page < 1) throw new InvalidPaginationException("Page must be at least 1");
        if (size < 1 || size > 50) throw new InvalidPaginationException("Size must be between 1 and 50");
    }

    private static ProductReviewResponse toResponse(ProductReview review) {
        return new ProductReviewResponse(review.getId(), review.getProductId(), review.getUserId(), review.getUserName(),
                review.getRating(), review.getComment(), review.getCreatedAt(), review.getUpdatedAt());
    }
}
