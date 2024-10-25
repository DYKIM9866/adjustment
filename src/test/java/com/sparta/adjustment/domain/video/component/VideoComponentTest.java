package com.sparta.adjustment.domain.video.component;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VideoComponentTest {

    String userEmail = "xxxx@gmail.com";
    Long videoId = 1L;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private VideoComponent videoComponent;

    @Test
    @DisplayName("Redis 값 저장")
    void setRedisString(){
        //given

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        //when
        videoComponent.setCached(userEmail, videoId);

        //then
        verify(valueOperations).set(userEmail + ":" + videoId, "1");
    }

    @Test
    @DisplayName("캐시가 존재하는지 확인")
    void getRedisStringValue(){
        //given
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("userEmail:1")).thenReturn(null);
        when(valueOperations.get("xxxx@gmail.com:1")).thenReturn("1");

        //when
        boolean test1 = videoComponent.isWatchCachedEmpty("userEmail", 1L);
        boolean test2 = videoComponent.isWatchCachedEmpty("xxxx@gmail.com", 1L);

        //then
        assertThat(test1).isEqualTo(true);
        assertThat(test2).isEqualTo(false);
    }
}