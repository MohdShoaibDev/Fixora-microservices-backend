package com.shoaib.authservice.utility;

import com.shoaib.authservice.dto.AddressResponseDto;
import com.shoaib.authservice.dto.UserRegisterDto;
import com.shoaib.authservice.dto.UserResponseDto;
import com.shoaib.authservice.entity.Address;
import com.shoaib.authservice.entity.User;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Mapper {
    private Mapper(){}
    public static UserResponseDto mapToUserResponseDtoFromUser(User user){
        return UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .number(user.getPhone())
                .fullname(user.getFullname())
                .profileImage(user.getProfileImage())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public static User mapToUserFromUserRegisterDto(UserRegisterDto userRegisterDto){
        if(userRegisterDto.getFullname() == null || userRegisterDto.getPassword() == null ) return null;
        return User.builder()
                .email(userRegisterDto.getEmail().toLowerCase())
                .fullname(userRegisterDto.getFullname())
                .phone(userRegisterDto.getNumber())
                .password(userRegisterDto.getPassword())
                .build();
    }

    public static AddressResponseDto mapToAddressResponseDto(Address address) {
        if (address == null) {
            return null;
        }

        return AddressResponseDto.builder()
                .id(address.getId())
                .fullName(address.getFullName())
                .mobile(address.getMobile())
                .houseNumber(address.getHouseNumber())
                .building(address.getBuilding())
                .street(address.getStreet())
                .landmark(address.getLandmark())
                .city(address.getCity())
                .state(address.getState())
                .pincode(address.getPincode())
                .country(address.getCountry())
                .latitude(address.getLatitude())
                .longitude(address.getLongitude())
                .defaultAddress(address.getDefaultAddress())
                .createdAt(address.getCreatedAt())
                .updatedAt(address.getUpdatedAt())
                .build();
    }
}
