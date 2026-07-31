package com.shoaib.productservice.repository;

import com.shoaib.productservice.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, UUID> {
    boolean existsByNameAndActiveIsTrue(String name);
    List<ProductCategory> findByActiveIsTrue();
}
