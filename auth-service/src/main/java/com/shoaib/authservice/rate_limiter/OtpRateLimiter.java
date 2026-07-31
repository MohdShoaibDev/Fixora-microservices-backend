package com.shoaib.authservice.rate_limiter;

import com.shoaib.redis.RedisKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class OtpRateLimiter {

    private final StringRedisTemplate stringRedisTemplate;

    public boolean canGenerateOtp(String email){

        long MAX_OTP_COUNT_PER_DAY = 30L;

        String key = RedisKeys.OTP_RATE_LIMIT + email;
        if(!stringRedisTemplate.hasKey(key)){
            stringRedisTemplate.opsForValue().set(key, "1", 24, TimeUnit.HOURS);
            return true;
        }
        long count = stringRedisTemplate.opsForValue().increment(key);
        return count <= MAX_OTP_COUNT_PER_DAY;
    }
}
