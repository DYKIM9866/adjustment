package com.sparta.adjustment.domain.video.component;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VideoRedisComponent {

    private final RedisTemplate<String, Object> redisTemplate;

    public Integer increaseWatched(Long videoId){
        Integer value = getValue(String.valueOf(videoId));
        if(value == null){
            setValueOne(String.valueOf(videoId));
            return 1;
        }else{
            upValue(String.valueOf(videoId));
            return value+1;
        }
    }

    public void setWatchCached(Long userId, Long videoId){
        setValueOne(userId+":"+videoId);
    }

    public Integer getWatchCached(Long userId, Long videoId){
        return getValue(userId +":" +videoId);
    }

    private Integer getValue(String key){
        ValueOperations<String, Object> ssvo = redisTemplate.opsForValue();
        return (Integer)ssvo.get(key);
    }
    private void upValue(String key){
        ValueOperations<String, Object> ssvo = redisTemplate.opsForValue();
        ssvo.increment(key);
    }
    private void setValueOne(String key){
        ValueOperations<String, Object> ssvo = redisTemplate.opsForValue();
        ssvo.set(key, 1);
    }
}
