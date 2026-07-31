package com.shoaib.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisAuthHelperMethods {
    private final StringRedisTemplate stringRedisTemplate;

    public boolean isTokenBlacklisted(String token) {
        String key = RedisKeys.LOGOUT_PREFIX + token;
        return stringRedisTemplate.hasKey(key);
    }
}
