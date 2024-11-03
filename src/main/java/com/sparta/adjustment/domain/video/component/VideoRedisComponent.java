package com.sparta.adjustment.domain.video.component;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class VideoRedisComponent {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ValueOperations<String, Object> ops = redisTemplate.opsForValue();

    public Object getCached(String key) {
        return ops.get(key);
    }

    public void increaseCached(String key) {
        ops.increment(key);
    }

    public void setCached(String key, Object value, int time) {
        ops.set(key, value, time, TimeUnit.SECONDS);
    }
}
