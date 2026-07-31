package com.shoaib.cartservice.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddItemCartDto {
    @NotNull(message = "ProductId is required")
    private UUID productId;

    @NotNull(message = "BookingDate is required")
    private LocalDate bookingDate;

    @NotNull(message = "BookingTime is required")
    private LocalTime  bookingTime;
}
