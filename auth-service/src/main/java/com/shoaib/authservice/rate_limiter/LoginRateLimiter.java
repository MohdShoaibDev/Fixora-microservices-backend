package com.shoaib.authservice.rate_limiter;

import com.shoaib.redis.RedisKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class LoginRateLimiter {

    private final StringRedisTemplate stringRedisTemplate;
    private final long MAX_LOGIN_RATE_LIMIT_TIME = 20L;

    public void handleLoginRateLimit(String email){
        String key = RedisKeys.LOGIN_RATE_LIMIT + email;
        if(!stringRedisTemplate.hasKey(key)){
            stringRedisTemplate.opsForValue().set(key, "1", 24, TimeUnit.HOURS);
        }else{
            stringRedisTemplate.opsForValue().increment(key);
        }
    }

    public void resetLoginRateLimit(String email){
        String key = RedisKeys.LOGIN_RATE_LIMIT + email;
        stringRedisTemplate.delete(key);
    }

    public boolean canUserLogin(String email){
        String key = RedisKeys.LOGIN_RATE_LIMIT + email;
        String value = stringRedisTemplate.opsForValue().get(key);
        if(value == null){
            return false;
        }
        long count = Long.parseLong(value);
        return count >= MAX_LOGIN_RATE_LIMIT_TIME;
    }
}
