package com.shoaib.productservice.service.productService;

import com.shoaib.cart.ClientCartItemRequest;
import com.shoaib.order.OrderProductDtoRequest;
import com.shoaib.productDtos.ProductClientDto;
import com.shoaib.productDtos.ProductIdListDto;
import com.shoaib.productDtos.ReserveProductDto;
import com.shoaib.productservice.dtos.*;
import com.shoaib.productservice.entity.Product;
import com.shoaib.productservice.mapper.Mapper;
import com.shoaib.productservice.repository.ProductCategoryRepository;
import com.shoaib.productservice.repository.ProductRepository;
import com.shoaib.productservice.repository.ProductReserveRepository;
import com.shoaib.productservice.service.productReview.ProductReviewServiceImpl;
import com.shoaib.productservice.specification.ProductSpecification;
import com.shoaib.productservice.utility.SortDirection;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductReviewServiceImpl productReviewServiceImpl;

    @Override
    public void addProduct(ProductRequestDto productRequestDto) {
        boolean categoryExist = productCategoryRepository.existsById(productRequestDto.getCategoryId());
        if(!categoryExist){
            throw new RuntimeException("Category does not exist for this product");
        }
        var product = Mapper.mapProductRequestDtoToProduct(productRequestDto);
        productRepository.save(product);
    }

    @Override
    public PageResponseDto getProducts(ProductFilterRequest  productFilterRequest) {
        Specification<Product> productSpecification = ProductSpecification.withFilters(productFilterRequest);
        Pageable pageable;
        if(productFilterRequest.getSortBy() == null){
            pageable = PageRequest.of(productFilterRequest.getPage() - 1, 10);
        }else{
            pageable = PageRequest.of(productFilterRequest.getPage() - 1, 10, Sort.by(
                    SortDirection.DESC == productFilterRequest.getSortBy() ? Sort.Direction.DESC : Sort.Direction.ASC, "price"));
        }
        Page<Product> productPage = productRepository.findAll(productSpecification, pageable);
        List<Product> productList = new ArrayList<>(productPage.getContent());
      try{
          if(productFilterRequest.getCategoryId() != null && productFilterRequest.getPage() == 1){
              Product product = productRepository.findByCategoryIdAndActiveIsTrueAndGeneralPurposeIsTrue(productFilterRequest.getCategoryId()).orElseThrow(() -> new RuntimeException("Category does not exist for this product"));
              productList.addFirst(product);
          }
      }catch (Exception e){
          System.out.println(e.getMessage());
      }
        return PageResponseDto.builder()
                .data(productList.stream().map(Mapper::mapProductToProductResponseDto).toList())
                .totalPages(productPage.getTotalPages())
                .page(productPage.getNumber())
                .totalElements(productPage.getTotalElements() + 1)
                .build();
    }

    @Override
    public ProductResponseDto getProduct(UUID id) {
        var product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        ProductResponseDto productResponseDto = Mapper.mapProductToProductResponseDto(product);
        Page<ProductReviewResponse> productReviewResponsePage = productReviewServiceImpl
                .getProductReviews(id, 1, 5);
        productResponseDto.setProductReviewResponseList(productReviewResponsePage.getContent());
        return productResponseDto;
    }

    @Override
    public List<ProductClientDto> getProductsDetails(ProductIdListDto productIdListDto) {
        List<Product> productList = productRepository
                .findByIdInAndActiveIsTrueOrderByCreatedAtDesc(productIdListDto.getListId());
        return productList.stream().map(Mapper::mapProductToProductClientDto).toList();
    }

    @Override
    @Transactional
    public HashMap<UUID, ReserveProductDto> getProductsPrice( OrderProductDtoRequest<List<ClientCartItemRequest>> orderProductDtoRequest) {
        List<Product> productList = productRepository.findByIdInAndActiveIsTrue(orderProductDtoRequest.getData()
                .stream().map(ClientCartItemRequest::getProductId).toList());

        HashMap<UUID, ReserveProductDto> productMap = new HashMap<>();

        for (Product product : productList) {
            productMap.put(product.getId(), ReserveProductDto.createReserveProductDto(
                    product.getName(),
                    product.getDescription(),
                    product.getThumbnailUrl(),
                    product.getPrice()
            ) );
        }

        return productMap;
    }

    @Override
    public ProductClientDto getGeneralInspectionProductsDetailsByCategoryId(UUID categoryId) {
        Product product = productRepository.findByCategoryIdAndActiveIsTrueAndGeneralPurposeIsTrue(categoryId)
                .orElseThrow(()  -> new RuntimeException("Category does not exist for this product"));
        return Mapper.mapProductToProductClientDto(product);
    }
}
