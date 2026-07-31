package com.shoaib.authservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequestDto {

    private UUID id;

    @NotBlank(message = "Full name is required")
    @Size(max = 100)
    private String fullName;

    @NotBlank(message = "Mobile number is required")
    @Pattern(
            regexp = "^[6-9]\\d{9}$",
            message = "Invalid mobile number"
    )
    private String mobile;

    @NotBlank(message = "House number is required")
    @Size(max = 100)
    private String houseNumber;

    @Size(max = 150)
    private String building;

    @NotBlank(message = "Street/Area is required")
    @Size(max = 200)
    private String street;

    @Size(max = 200)
    private String landmark;

    @NotBlank(message = "City is required")
    @Size(max = 100)
    private String city;

    @NotBlank(message = "State is required")
    @Size(max = 100)
    private String state;

    @NotBlank(message = "Pincode is required")
    @Pattern(
            regexp = "^\\d{6}$",
            message = "Invalid pincode"
    )
    private String pincode;

    @NotBlank(message = "Country is required")
    @Size(max = 100)
    private String country;

    private Double latitude;

    private Double longitude;

    private boolean defaultAddress;
}
