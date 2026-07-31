package com.shoaib.payment;


import com.shoaib.util.enums.Currency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class RazorpayOrderIdRequestDto {
    @NotNull(message = "Booking ID is required")
    private UUID bookingId;

    @NotNull(message = "Currency is required")
    private Currency currency;

    @NotNull(message = "Currency cannot be null")
    @PositiveOrZero(message = "Amount cannot be less than 0")
    private BigDecimal amount;
}
