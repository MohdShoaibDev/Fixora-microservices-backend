package com.shoaib.productservice.service.recentView;

import com.shoaib.productservice.dtos.ProductResponseDto;
import com.shoaib.productservice.entity.Product;
import com.shoaib.productservice.entity.RecentViewProduct;
import com.shoaib.productservice.mapper.Mapper;
import com.shoaib.productservice.repository.ProductRepository;
import com.shoaib.productservice.repository.RecentViewProductRepository;
import com.shoaib.productservice.service.productService.ProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.SortDirection;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RecentViewProductServiceImpl implements RecentViewProductService {

    private final RecentViewProductRepository recentViewProductRepository;
    private final ProductRepository productRepository;

    @Override
    public void recentViewProduct(UUID userId, UUID productId) {
        RecentViewProduct recentViewProduct = recentViewProductRepository.findByUserIdAndProductId(userId,productId).orElse(
                RecentViewProduct.builder()
                        .userId(userId)
                        .productId(productId)
                        .build()
        );
        recentViewProduct.setCreatedAt(LocalDateTime.now());
        recentViewProductRepository.save(recentViewProduct);
        List<RecentViewProduct> list = recentViewProductRepository.findTop20ByUserIdOrderByCreatedAtAsc(userId);
        if(list.size() > 20){
            RecentViewProduct targetRecentViewProduct = recentViewProductRepository.findFirstByUserIdOrderByCreatedAtDesc(userId);
            recentViewProductRepository.delete(targetRecentViewProduct);
        }
    }

    @Override
    @Transactional
    public List<ProductResponseDto> getRecentView(UUID userId) {
        List<RecentViewProduct> list = recentViewProductRepository.findByUserId(userId);
        if(list.isEmpty()){
            throw new RuntimeException("No recent view product found");
        }
        List<UUID> uuidList = new ArrayList<>();
        for (RecentViewProduct recentViewProduct : list) {
            uuidList.add(recentViewProduct.getProductId());
        }
        List<Product> productList = productRepository.findByIdInAndActiveIsTrue(uuidList, Sort.by(Sort.Direction.DESC, "createdAt"));
        return productList.stream().map(Mapper::mapProductToProductResponseDto).toList();
    }

}
