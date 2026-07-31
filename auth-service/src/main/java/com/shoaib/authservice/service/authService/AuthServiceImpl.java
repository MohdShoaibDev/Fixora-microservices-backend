package com.shoaib.authservice.service.authService;

import com.shoaib.authservice.dto.*;
import com.shoaib.authservice.entity.User;
import com.shoaib.authservice.kafka.KafkaProducer;
import com.shoaib.authservice.rate_limiter.LoginRateLimiter;
import com.shoaib.authservice.rate_limiter.OtpRateLimiter;
import com.shoaib.authservice.repository.RefreshTokenRepository;
import com.shoaib.authservice.repository.UserRepository;
import com.shoaib.authservice.security.CustomUserDetails;
import com.shoaib.authservice.security.RefreshTokenService;
import com.shoaib.authservice.utility.Mapper;
import com.shoaib.authservice.utility.UserStatus;
import com.shoaib.kafka.dtos.KafkaEnvelope;
import com.shoaib.kafka.dtos.RegisterRequest;
import com.shoaib.kafka.util.KafkaEventType;
import com.shoaib.kafka.util.KafkaTopics;
import com.shoaib.redis.RedisKeys;
import com.shoaib.security.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;
    private final OtpRateLimiter otpRateLimiter;
    private final LoginRateLimiter loginRateLimiter;
    private final RefreshTokenService  refreshTokenService;
    private final KafkaProducer kafkaProducer;

    @Override
    public void registerUser(UserRegisterDto registerRequestDto) {
       boolean isUserPresentWithEmail = userRepository.existsByEmail(registerRequestDto.getEmail());
        if (isUserPresentWithEmail) {
            throw new RuntimeException("User with email " + registerRequestDto.getEmail() + " already exists");
        }
        if(registerRequestDto.getNumber() != null){
            boolean isUserPresentWithPhone = userRepository.existsByPhone(registerRequestDto.getNumber());
            if (isUserPresentWithPhone) {
                throw new RuntimeException("User with phone " + registerRequestDto.getNumber() + " already exists");
            }
        }
        Integer otp = generateOtp(registerRequestDto.getEmail());
        registerRequestDto.setPassword(passwordEncoder.encode(registerRequestDto.getPassword()));
        String key = RedisKeys.OTP_PREFIX + registerRequestDto.getEmail();
        TempUserRegisterDto  tempUserRegisterDto = TempUserRegisterDto.builder()
                .userRegisterDto(registerRequestDto)
                .otp(String.valueOf(otp))
                .build();
        String json = objectMapper.writeValueAsString(tempUserRegisterDto);
        stringRedisTemplate.opsForValue().set(key, json, 300, TimeUnit.SECONDS);
        var user = RegisterRequest.builder()
                .email(registerRequestDto.getEmail())
                .otp(String.valueOf(otp))
                .build();
        kafkaProducer.send(KafkaTopics.USER, registerRequestDto.getEmail(), new KafkaEnvelope<>(KafkaEventType.USER_ONBOARDING, user));
    }

    @Override
    public AuthApiResponseDto<UserResponseDto> login(UserLoginDto userLoginDto) {
        boolean isUserPresentWithEmail = loginRateLimiter.canUserLogin(userLoginDto.getEmail());
        if (isUserPresentWithEmail) {
            throw new RuntimeException("Maximum login rate limit reached");
        }
        CustomUserDetails user = null;
        try{
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    userLoginDto.getEmail(), userLoginDto.getPassword()));
            user = (CustomUserDetails) authentication.getPrincipal();
            loginRateLimiter.resetLoginRateLimit(userLoginDto.getEmail());
        }catch (RuntimeException e){
            loginRateLimiter.handleLoginRateLimit(userLoginDto.getEmail());
            System.out.println(e.getMessage());
            throw new RuntimeException("Invalid username or password");
        }
        assert user != null;
        UserResponseDto userResponseDto = Mapper.mapToUserResponseDtoFromUser(user.getUser());
        return AuthApiResponseDto.<UserResponseDto>builder()
                .message("User login successfully")
                .status(true)
                .data(userResponseDto)
                .token(jwtUtil.generateToken(user.getUser().getId(), user.getUser().getRole().name()))
                .refreshToken(refreshTokenService.createRefreshToken(user.getUser().getId()))
                .build();
    }

    public void resendOtp(ResentOtpRequestDto resentOtpRequestDto){
       try{
           String key = RedisKeys.OTP_PREFIX + resentOtpRequestDto.getEmail();
           String json = stringRedisTemplate.opsForValue().get(key);
           if(json == null){
               throw new RuntimeException("Register again");
           }
           TempUserRegisterDto tempUserRegisterDto = objectMapper.readValue(json, TempUserRegisterDto.class);
           Integer otp = generateOtp(resentOtpRequestDto.getEmail());
           tempUserRegisterDto.setOtp(String.valueOf(otp));
           String json2 = objectMapper.writeValueAsString(tempUserRegisterDto);
           stringRedisTemplate.opsForValue().set(key, json2, 300, TimeUnit.SECONDS);
           var user = RegisterRequest.builder()
                   .email(resentOtpRequestDto.getEmail())
                   .otp(String.valueOf(otp))
                   .build();
           kafkaProducer.send(KafkaTopics.USER, resentOtpRequestDto.getEmail(), new KafkaEnvelope<>(KafkaEventType.USER_ONBOARDING, user));
       }catch (Exception e){
           System.out.println(e.getMessage());
       }
    }

    public void forgotPassword(ResentOtpRequestDto resentOtpRequestDto){
        boolean userExists = userRepository.existsByEmail(resentOtpRequestDto.getEmail());
        if(!userExists){
            return;
        }
        int otp = generateOtp(resentOtpRequestDto.getEmail());
        String key = RedisKeys.OTP_PREFIX + resentOtpRequestDto.getEmail();
        TempUserRegisterDto  tempUserRegisterDto = TempUserRegisterDto.builder()
                .userRegisterDto(null)
                .otp(String.valueOf(otp))
                .build();
        String json = objectMapper.writeValueAsString(tempUserRegisterDto);
        stringRedisTemplate.opsForValue().set(key, json, 300, TimeUnit.SECONDS);
        var user = RegisterRequest.builder()
                .email(resentOtpRequestDto.getEmail())
                .otp(String.valueOf(otp))
                .build();
        kafkaProducer.send(KafkaTopics.USER, resentOtpRequestDto.getEmail(), new KafkaEnvelope<>(KafkaEventType.USER_ONBOARDING, user));
    }

    public Integer generateOtp(String  email) {
        boolean canGenerateOtp = otpRateLimiter.canGenerateOtp(email);
        if(!canGenerateOtp){
            throw new RuntimeException("Your otp limit reached, try later");
        }
        Random random = new Random();
        return random.nextInt(9000) + 1000;
    }

    public AuthApiResponseDto<UserResponseDto> verifyOtp(VerifyOtpRequestDto verifyOtpRequestDto) {
        String json = stringRedisTemplate.opsForValue().get(RedisKeys.OTP_PREFIX + verifyOtpRequestDto.getEmail());
        if(json != null){
            TempUserRegisterDto tempUserRegisterDto = objectMapper.readValue(json, TempUserRegisterDto.class);
            if(tempUserRegisterDto.getOtp().equals(verifyOtpRequestDto.getOtp())){
                User user = Mapper.mapToUserFromUserRegisterDto(tempUserRegisterDto.getUserRegisterDto());
                if(user == null){
                    throw new RuntimeException("Arguments validation failed");
                }
                userRepository.save(user);
                stringRedisTemplate.delete(RedisKeys.OTP_PREFIX + verifyOtpRequestDto.getEmail());
                UserResponseDto userResponseDto = UserResponseDto.builder()
                        .id(user.getId())
                        .fullname(user.getFullname())
                        .email(user.getEmail())
                        .number(user.getPhone())
                        .profileImage(user.getProfileImage())
                        .createdAt(user.getCreatedAt())
                        .updatedAt(user.getUpdatedAt())
                        .build();
                return AuthApiResponseDto.<UserResponseDto>builder()
                        .message("User registered successfully")
                        .status(true)
                        .data(userResponseDto)
                        .token(jwtUtil.generateToken(user.getId(), user.getRole().name()))
                        .refreshToken(refreshTokenService.createRefreshToken(user.getId()))
                        .build();
            }
        }
        throw new RuntimeException("Otp verification failed");
    }

    @Override
    public AuthApiResponseDto<Object> verifyForgotPasswordOtp(VerifyOtpRequestDto verifyOtpRequestDto) {
        String json = stringRedisTemplate.opsForValue().get(RedisKeys.OTP_PREFIX + verifyOtpRequestDto.getEmail());
        User user = userRepository.findByEmailAndIsActive(verifyOtpRequestDto.getEmail(), UserStatus.ACTIVE).orElseThrow(() ->
                new  RuntimeException("User not found"));
        if(json != null){
            TempUserRegisterDto tempUserRegisterDto = objectMapper.readValue(json, TempUserRegisterDto.class);
            if(tempUserRegisterDto.getOtp().equals(verifyOtpRequestDto.getOtp())){
                stringRedisTemplate.delete(RedisKeys.OTP_PREFIX + verifyOtpRequestDto.getEmail());
                return AuthApiResponseDto.builder()
                        .status(true)
                        .message("Otp verified")
                        .token(refreshTokenService.createRefreshToken(user.getId(), Duration.ofMinutes(5L)))
                        .refreshToken(null)
                        .data(null)
                        .build();
            }
        }
        throw new RuntimeException("Otp verification failed");
    }

    @Override
    public void forgotPasswordReset(PasswordResetDto passwordResetDto, String token) {
        User user = userRepository.findByEmailAndIsActive(passwordResetDto.getEmail(), UserStatus.ACTIVE)
                .orElseThrow(() -> new  RuntimeException("User not found"));
        UUID userId = refreshTokenService.validateAndConsumeRefreshToken(token);
        if(userId.equals(user.getId())){
            user.setPassword(passwordEncoder.encode(passwordResetDto.getPassword()));
            userRepository.save(user);
            return;
        }
        throw new RuntimeException("Password reset failed");
    }


    public void logout(String token, String rawRefreshToken) {
        String key = RedisKeys.LOGOUT_PREFIX + token;
        Date expiration = jwtUtil.extractExpiration(token);
        long remainingTime =
                expiration.getTime() - System.currentTimeMillis();
        stringRedisTemplate.opsForValue().set(key, "logout", remainingTime,TimeUnit.MILLISECONDS);
        refreshTokenService.revokeRefreshToken(rawRefreshToken);
    }

    @Transactional
    public AuthApiResponseDto<Object> refreshToken(
            String rawRefreshToken
    ) {
        UUID userId =
                refreshTokenService
                        .validateAndConsumeRefreshToken(
                                rawRefreshToken
                        );

        User user =
                userRepository
                        .findByIdAndIsActive(userId, UserStatus.ACTIVE)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found or inactive"
                                )
                        );

        String newAccessToken =
                jwtUtil.generateToken(
                        user.getId(),
                        user.getRole().name()
                );

        String newRefreshToken =
                refreshTokenService
                        .createRefreshToken(
                                user.getId()
                        );

        return AuthApiResponseDto.builder()
                .token(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    @Transactional
    public int deleteExpiredAndConsumedTokens() {
        return refreshTokenRepository.deleteExpiredConsumedAndRevokedTokens(
                LocalDateTime.now()
        );
    }
}
