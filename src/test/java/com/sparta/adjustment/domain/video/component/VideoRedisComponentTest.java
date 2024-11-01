package com.sparta.adjustment.domain.video.component;

import org.junit.jupiter.api.BeforeEach;
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
class VideoRedisComponentTest {

    Long userId = 3224L;
    Long videoId = 1L;

    @Mock
    private RedisTemplate<String, Integer> redisTemplate;

    @Mock
    private ValueOperations<String, Integer> valueOperations;

    @InjectMocks
    private VideoRedisComponent videoRedisComponent;

    @BeforeEach
    void setValueOperations(){
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    @DisplayName("Redis WatchedCached 저장")
    void setRedisString(){
        //given
        //when
        videoRedisComponent.setWatchCached(userId, videoId);

        //then
        verify(valueOperations).set(userId + ":" + videoId, 1);
    }

    @Test
    @DisplayName("캐시가 존재하는지 확인")
    void checkCached(){
        //given
        when(valueOperations.get(userId + ":" + videoId)).thenReturn(null);

        //when
        Integer test1 = videoRedisComponent.getWatchCached(userId, videoId);

        //then
        assertThat(test1).isEqualTo(null);
    }

    @Test
    @DisplayName("조회수 증가 테스트")
    void watchedIncrease(){
        //given
        when(valueOperations.get(String.valueOf(videoId))).thenReturn(0);
        //when
        Integer integer = videoRedisComponent.increaseWatched(videoId);
        //then
        assertThat(integer).isEqualTo(1);
        //

    }
}