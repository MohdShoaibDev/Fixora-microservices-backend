package com.shoaib.productservice.service.productCategory;

import com.shoaib.productservice.dtos.ProductCategoryRequestDto;
import com.shoaib.productservice.dtos.ProductCategoryResponseDto;
import com.shoaib.productservice.entity.ProductCategory;
import com.shoaib.productservice.mapper.Mapper;
import com.shoaib.productservice.repository.ProductCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductCategoryServiceImpl implements ProductCategoryService {

    private final ProductCategoryRepository productCategoryRepository;

    @Override
    public List<ProductCategoryResponseDto> getCategories() {
        List<ProductCategory> productCategoriesList = productCategoryRepository.findByActiveIsTrue();
        return productCategoriesList.stream().map(Mapper::mapProductCategoryToProductCategoryResponseDto).toList();
    }

    @Override
    public void addCategory(ProductCategoryRequestDto categoryDto) {
        boolean categoryExists = productCategoryRepository.existsByNameAndActiveIsTrue(categoryDto.getName().toLowerCase());
        if(categoryExists){
            throw new RuntimeException("Category already exists");
        }
        ProductCategory productCategory = Mapper.mapProductCategoryDtoToProductCategory(categoryDto);
        productCategoryRepository.save(productCategory);
    }

    @Override
    public void deleteCategory(UUID categoryId) {
        ProductCategory categoryExists = productCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category does not exist"));
        categoryExists.setActive(false);
        productCategoryRepository.save(categoryExists);
    }
}
