package com.shoaib.productservice.service.productCategory;

import com.shoaib.productservice.dtos.ProductCategoryRequestDto;
import com.shoaib.productservice.dtos.ProductCategoryResponseDto;

import java.util.List;
import java.util.UUID;

public interface ProductCategoryService {
    void addCategory(ProductCategoryRequestDto categoryDto);
    void deleteCategory(UUID categoryId);
    List<ProductCategoryResponseDto> getCategories();
}
