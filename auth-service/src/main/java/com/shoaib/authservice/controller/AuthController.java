package com.shoaib.authservice.controller;

import com.shoaib.apiResponse.ApiResponse;
import com.shoaib.authservice.dto.*;
import com.shoaib.authservice.service.authService.AuthServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("auth/")
public class AuthController {

    private final AuthServiceImpl  authServiceImpl;

    @PostMapping("logout")
    public ResponseEntity<ApiResponse<Object>> logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String  authHeader,
                                                      @RequestHeader("Refresh-Token") String  refreshToken) {
        authServiceImpl.logout(authHeader.substring(7).trim(),refreshToken);
        return ResponseEntity.ok()
                .body(new ApiResponse<>(true, "User logout successfully", null));
    }

    @PostMapping("login")
    public ResponseEntity<AuthApiResponseDto<UserResponseDto>> login(@Valid @RequestBody UserLoginDto userLoginDto) {
        return ResponseEntity.ok()
                .body(authServiceImpl.login(userLoginDto));
    }

    @PostMapping("register")
    public ResponseEntity<ApiResponse<UserOtpResponseDto>> register(@Valid @RequestBody UserRegisterDto registerRequestDto) {
        authServiceImpl.registerUser(registerRequestDto);
        return ResponseEntity.ok()
                .body(new ApiResponse<>(true,
                        "Otp sent successfully", null));
    }

    @PostMapping("resend-otp")
    public ResponseEntity<ApiResponse<Object>> resendOtp(@Valid @RequestBody ResentOtpRequestDto resentOtpRequestDto) {
        authServiceImpl.resendOtp(resentOtpRequestDto);
        return ResponseEntity.ok()
                .body(new ApiResponse<>(true,
                        "Otp sent successfully", null));
    }

    @PostMapping("forgot-password")
    public ResponseEntity<ApiResponse<Object>> forgotPassword(@Valid @RequestBody ResentOtpRequestDto resentOtpRequestDto) {
        authServiceImpl.forgotPassword(resentOtpRequestDto);
        return ResponseEntity.ok()
                .body(new ApiResponse<>(true,
                        "Otp sent successfully", null));
    }

    @PostMapping("verify-forgot-password-otp")
    public ResponseEntity<ApiResponse<AuthApiResponseDto<Object>>> verifyForgotPasswordOtp(@Valid @RequestBody VerifyOtpRequestDto verifyOtpRequestDto) {
        return ResponseEntity.ok()
                .body(new ApiResponse<>(true,
                        "Otp verified successfully", authServiceImpl.verifyForgotPasswordOtp(verifyOtpRequestDto)));
    }

    @PostMapping("reset-forgot-password")
    public ResponseEntity<ApiResponse<Object>> resetForgotPassword(@Valid @RequestBody PasswordResetDto passwordResetDto,
                                                                                       @RequestHeader("Password-Reset-Token") String resetToken) {
        authServiceImpl.forgotPasswordReset(passwordResetDto, resetToken);
        return ResponseEntity.ok()
                .body(new ApiResponse<>(true,
                        "Password reset successfully", null));
    }

    @PostMapping("verify-otp")
    public ResponseEntity<ApiResponse<AuthApiResponseDto<UserResponseDto>>> verifyOtp(@Valid @RequestBody VerifyOtpRequestDto verifyOtpRequestDto) {
        return ResponseEntity.ok()
                .body(new ApiResponse<>(true,
                        "Account created successfully", authServiceImpl.verifyOtp(verifyOtpRequestDto)));
    }

    @PostMapping("refresh-token")
    public ResponseEntity<ApiResponse<AuthApiResponseDto<Object>>> refreshToken(@RequestBody RefreshTokenDto refreshTokenDto) {
        return ResponseEntity.ok().body(new ApiResponse<>(true, "Access token regenerate successfully", authServiceImpl.refreshToken(refreshTokenDto.getRefreshToken())));
    }

    @Scheduled(cron = "0 */30 * * * *")
    public void removeRefreshToken() {
        authServiceImpl
                .deleteExpiredAndConsumedTokens();
    }
}
