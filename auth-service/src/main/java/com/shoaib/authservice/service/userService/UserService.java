package com.shoaib.authservice.service.userService;

import com.shoaib.authservice.dto.AddressRequestDto;
import com.shoaib.authservice.dto.AddressResponseDto;
import com.shoaib.authservice.dto.UserResponseDto;
import com.shoaib.authservice.entity.Address;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserResponseDto getUser(UUID userId);
    List<AddressResponseDto> getUserAddresses(UUID userId);
    void addAddress(AddressRequestDto address, UUID userId);
    void updateAddress(AddressRequestDto addressRequestDto, UUID userId);
    void deleteAddress(UUID addressId, UUID userId);
}
