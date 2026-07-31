package com.shoaib.productservice.service.productService;

import com.shoaib.cart.ClientCartItemRequest;
import com.shoaib.order.OrderProductDtoRequest;
import com.shoaib.productDtos.ProductClientDto;
import com.shoaib.productDtos.ProductIdListDto;
import com.shoaib.productDtos.ReserveProductDto;
import com.shoaib.productservice.dtos.*;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public interface ProductService {
    void addProduct(ProductRequestDto productRequestDto);
    PageResponseDto getProducts(ProductFilterRequest productFilterRequest);
    ProductResponseDto getProduct(UUID id);
    HashMap<UUID, ReserveProductDto> getProductsPrice(OrderProductDtoRequest<List<ClientCartItemRequest>> orderProductDtoRequest);
    List<ProductClientDto> getProductsDetails(ProductIdListDto productIdListDto);
    ProductClientDto getGeneralInspectionProductsDetailsByCategoryId(UUID categoryId);
}
