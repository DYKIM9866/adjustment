package com.sparta.adjustment.domain.video.component;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class VideoRedisComponent {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ValueOperations<String, Object> ops = redisTemplate.opsForValue();

    public Object getCached(String key) {
        return ops.get(key);
    }

    public boolean setCached(String key, Object value, int time) {
        return ops.setIfAbsent(key, value, time, TimeUnit.SECONDS);
    }

    public Object increaseCached(String key) {
        return ops.increment(key);
    }

    public Object decreaseCache(String key) {
        return ops.decrement(key);
    }
}
