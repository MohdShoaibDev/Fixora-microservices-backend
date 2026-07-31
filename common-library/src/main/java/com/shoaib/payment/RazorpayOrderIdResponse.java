package com.shoaib.payment;

import com.shoaib.util.enums.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RazorpayOrderIdResponse {
    private String gatewayOrderId;
    private BigDecimal amount;
    private Currency currency;
    private String keyId;
    private LocalDateTime createdAt;
}
