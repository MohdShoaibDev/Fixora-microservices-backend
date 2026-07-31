package com.shoaib.authservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
public class UserResponseDto {
    private UUID id;
    private String fullname;
    private String email;
    private String number;
    private String profileImage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private AddressResponseDto address;

    public void addAddress(AddressResponseDto addressResponseDto) {
        this.address = addressResponseDto;
    }
}
