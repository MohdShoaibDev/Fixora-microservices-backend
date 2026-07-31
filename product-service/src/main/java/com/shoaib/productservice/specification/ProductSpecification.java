package com.shoaib.productservice.specification;


import com.shoaib.productservice.dtos.ProductFilterRequest;
import com.shoaib.productservice.entity.Product;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public final class ProductSpecification {

    private ProductSpecification() {
    }

    public static Specification<Product> withFilters(
            ProductFilterRequest filter
    ) {
        return Specification
                .where(hasCategory(filter.getCategoryId()))
                .and(hasMinimumRating(filter.getMinRating()))
                .and(hasActiveStatus())
                .and(hasGeneralPurpose());
    }

    private static Specification<Product> hasCategory(UUID categoryId) {
        return (root, query, criteriaBuilder) -> {
            if (categoryId == null || categoryId.toString().isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(
                    root.get("categoryId"),
                    categoryId
            );
        };
    }

    private static Specification<Product> hasMinimumRating(
            Double minRating
    ) {
        return (root, query, criteriaBuilder) -> {
            if (minRating == null || minRating == 0.0) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.greaterThanOrEqualTo(
                    root.get("rating"),
                    minRating
            );
        };
    }

    private static Specification<Product> hasActiveStatus() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(
                root.get("active"),
                true
        );
    }

    private static Specification<Product> hasGeneralPurpose() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(
                root.get("generalPurpose"),
                false
        );
    }
}
