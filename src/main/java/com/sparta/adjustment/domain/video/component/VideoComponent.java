package com.sparta.adjustment.domain.video.component;

import com.sparta.adjustment.domain.video.Video;
import com.sparta.adjustment.domain.video.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class VideoComponent {

    private final VideoRepository videoRepository;
    private final RedisTemplate<String, String> redisTemplate;

    public Optional<Video> getVideo(Long videoId) {
        return videoRepository.findById(videoId);
    }

    public void setCached(String userEmail, Long videoId){
        ValueOperations<String, String> ssvo = redisTemplate.opsForValue();
        ssvo.set(userEmail + ":" + videoId, "1");
    }

    public boolean isWatchCachedEmpty(String userEmail, Long videoId){
        ValueOperations<String, String> ssvo = redisTemplate.opsForValue();
        String cached = ssvo.get(userEmail + ":" + videoId);
        return cached == null;
    }
}
