package com.shoaib.orderservice.dtos;

import com.shoaib.util.enums.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderIdDto {
    private UUID id;
    private String orderId;
    private BigDecimal amount;
    private Currency currency;
    private String keyId;
}
