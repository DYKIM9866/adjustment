package com.sparta.adjustment.domain.video.component;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VideoRedisComponent {

    private final RedisTemplate<String, Integer> redisTemplate;

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

    public void setWatchCached(String userEmail, Long videoId){
        setValueOne(userEmail+"+"+videoId);
    }

    public Integer getWatchCached(String userEmail, Long videoId){
        return getValue(userEmail +":" +videoId);
    }

    private Integer getValue(String key){
        ValueOperations<String, Integer> ssvo = redisTemplate.opsForValue();
        return ssvo.get(key);
    }
    private void upValue(String key){
        ValueOperations<String, Integer> ssvo = redisTemplate.opsForValue();
        ssvo.increment(key);
    }
    private void setValueOne(String key){
        ValueOperations<String, Integer> ssvo = redisTemplate.opsForValue();
        ssvo.set(key, 1);
    }
}
