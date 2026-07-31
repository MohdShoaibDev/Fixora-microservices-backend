package com.shoaib.productservice.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProductRatingAggregationTest {

    @Test
    void addUpdateAndRemoveRatingsWithoutScanningReviews() {
        Product product = Product.builder().build();

        product.addReviewRating(5);
        product.addReviewRating(3);
        assertThat(product.getRatingSum()).isEqualTo(8L);
        assertThat(product.getTotalReviews()).isEqualTo(2);
        assertThat(product.getTotalRating()).isEqualTo(4.0);

        product.updateReviewRating(3, 1);
        assertThat(product.getRatingSum()).isEqualTo(6L);
        assertThat(product.getTotalReviews()).isEqualTo(2);
        assertThat(product.getTotalRating()).isEqualTo(3.0);

        product.removeReviewRating(5);
        assertThat(product.getRatingSum()).isEqualTo(1L);
        assertThat(product.getTotalReviews()).isEqualTo(1);
        assertThat(product.getTotalRating()).isEqualTo(1.0);

        product.removeReviewRating(1);
        assertThat(product.getRatingSum()).isZero();
        assertThat(product.getTotalReviews()).isZero();
        assertThat(product.getTotalRating()).isZero();
    }

    @Test
    void ratingSumCannotBecomeNegative() {
        Product product = Product.builder().ratingSum(1L).totalReviews(2).totalRating(0.5).build();
        product.removeReviewRating(5);
        assertThat(product.getRatingSum()).isZero();
        assertThat(product.getTotalRating()).isZero();
    }
}
