package com.shoaib.authservice.controller;

import com.shoaib.apiResponse.ApiResponse;
import com.shoaib.authservice.dto.AddressRequestDto;
import com.shoaib.authservice.dto.AddressResponseDto;
import com.shoaib.authservice.dto.UserResponseDto;
import com.shoaib.authservice.service.authService.AuthServiceImpl;
import com.shoaib.authservice.service.userService.UserService;
import com.shoaib.security.JwtPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("user")
public class UserController {

    private final UserService userService;

    @GetMapping("/get-user")
    public ResponseEntity<ApiResponse<UserResponseDto>> getUserDetails(@AuthenticationPrincipal JwtPrincipal jwtPrincipal) {
        return ResponseEntity.ok()
                .body(new ApiResponse<>(true, "User details fetch successfully", userService.getUser(jwtPrincipal.userId())));
    }

    @GetMapping("/get-all-address")
    public ResponseEntity<ApiResponse<List<AddressResponseDto>>> getUserAllAddress(@AuthenticationPrincipal JwtPrincipal
                                                                                               jwtPrincipal) {
        return ResponseEntity.ok()
                .body(new ApiResponse<>(true, "User addresses fetch successfully",
                        userService.getUserAddresses(jwtPrincipal.userId())));
    }

    @PostMapping("/add-address")
    public ResponseEntity<ApiResponse<Object>> addUserAddress(@RequestBody @Valid AddressRequestDto addressRequestDto, @AuthenticationPrincipal JwtPrincipal jwtPrincipal) {
        userService.addAddress(addressRequestDto, jwtPrincipal.userId());
        return ResponseEntity.ok()
                .body(new ApiResponse<>(true, "User address added successfully", null));
    }

    @PutMapping("/update-address")
    public ResponseEntity<ApiResponse<Object>> updateUserAddress(@RequestBody @Valid AddressRequestDto addressRequestDto, @AuthenticationPrincipal JwtPrincipal jwtPrincipal) {
        userService.updateAddress(addressRequestDto, jwtPrincipal.userId());
        return ResponseEntity.ok()
                .body(new ApiResponse<>(true, "User address updated successfully", null));
    }

    @DeleteMapping("/delete-address/{id}")
    public ResponseEntity<ApiResponse<Object>> addUserAddress1(@PathVariable UUID id, @AuthenticationPrincipal JwtPrincipal jwtPrincipal) {
        userService.deleteAddress(id, jwtPrincipal.userId());
        return ResponseEntity.ok()
                .body(new ApiResponse<>(true, "User address deleted successfully", null));
    }
}
