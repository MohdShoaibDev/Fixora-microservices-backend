package com.shoaib.authservice.service.userService;

import com.shoaib.authservice.dto.AddressRequestDto;
import com.shoaib.authservice.dto.AddressResponseDto;
import com.shoaib.authservice.dto.UserResponseDto;
import com.shoaib.authservice.entity.Address;
import com.shoaib.authservice.entity.User;
import com.shoaib.authservice.repository.AddressRepository;
import com.shoaib.authservice.repository.UserRepository;
import com.shoaib.authservice.utility.Mapper;
import com.shoaib.authservice.utility.UserStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    @Override
    public UserResponseDto getUser(UUID userId) {
        User user = userRepository.findByIdAndIsActive(userId, UserStatus.ACTIVE).orElseThrow(() -> new RuntimeException("User not found with email: " + userId));
        UserResponseDto userResponseDto = Mapper.mapToUserResponseDtoFromUser(user);
        Address address = addressRepository.findByUserIdAndDefaultAddress(userId).orElseGet(() -> null);
        userResponseDto.addAddress(Mapper.mapToAddressResponseDto(address));
        return userResponseDto;
    }

    @Override
    public List<AddressResponseDto> getUserAddresses(UUID userId) {
        List<Address> addressList = addressRepository.findByUserId(userId);
        return addressList.stream().map(Mapper::mapToAddressResponseDto).toList();
    }

    @Override
    @Transactional
    public void addAddress(AddressRequestDto addressRequestDto, UUID userId) {

        if (addressRequestDto.isDefaultAddress()) {
            addressRepository.clearDefaultAddress(userId);
        }

        Address address = Address.create(
                userId,
                addressRequestDto.getFullName(),
                addressRequestDto.getMobile(),
                addressRequestDto.getHouseNumber(),
                addressRequestDto.getBuilding(),
                addressRequestDto.getStreet(),
                addressRequestDto.getLandmark(),
                addressRequestDto.getCity(),
                addressRequestDto.getState(),
                addressRequestDto.getPincode(),
                addressRequestDto.getCountry(),
                addressRequestDto.getLatitude(),
                addressRequestDto.getLongitude(),
                addressRequestDto.isDefaultAddress()
        );

        addressRepository.save(address);
    }

    @Override
    @Transactional
    public void updateAddress(AddressRequestDto addressRequestDto , UUID userId) {
        Address address = addressRepository.findById(addressRequestDto.getId())
                .orElseThrow(() -> new RuntimeException("Address not found with id: " + addressRequestDto.getId()));
        if(addressRequestDto.isDefaultAddress()) {
            addressRepository.clearDefaultAddress(userId);
        }
        address.update(
                addressRequestDto.getFullName(),
                addressRequestDto.getMobile(),
                addressRequestDto.getHouseNumber(),
                addressRequestDto.getBuilding(),
                addressRequestDto.getStreet(),
                addressRequestDto.getLandmark(),
                addressRequestDto.getCity(),
                addressRequestDto.getState(),
                addressRequestDto.getPincode(),
                addressRequestDto.getCountry(),
                addressRequestDto.getLatitude(),
                addressRequestDto.getLongitude(),
                addressRequestDto.isDefaultAddress()
        );
        addressRepository.save(address);
    }

    @Override
    public void deleteAddress(UUID addressId, UUID userId) {
        Address address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new RuntimeException("Address not found with id: " + addressId));
        addressRepository.delete(address);
    }

}
