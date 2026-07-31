package com.shoaib.authservice.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class AddressResponseDto {

    private UUID id;

    private String fullName;

    private String mobile;

    private String houseNumber;

    private String building;

    private String street;

    private String landmark;

    private String city;

    private String state;

    private String pincode;

    private String country;

    private Double latitude;

    private Double longitude;

    private boolean defaultAddress;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
