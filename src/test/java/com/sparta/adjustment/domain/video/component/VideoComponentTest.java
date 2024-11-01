package com.sparta.adjustment.domain.video.component;

import com.sparta.adjustment.domain.video.Video;
import com.sparta.adjustment.domain.video.repository.VideoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VideoComponentTest {

    @Mock
    private VideoRepository videoRepository;

    @InjectMocks
    private VideoComponent videoComponent;

    @Test
    @DisplayName("비디오가 존재하면 비디오 리턴한다.")
    void getVideo_whenVideoExist_returnVideo(){
        //given
        Long videoId = 1L;
        Video expect = new Video(); //id가 들어가 있다고 가정
        when(videoRepository.findById(videoId)).thenReturn(Optional.of(expect));

        //when
        Video actual = videoComponent.getVideo(videoId);

        //then
        assertEquals(expect, actual);
        verify(videoRepository, times(1)).findById(videoId);
    }

    @Test
    @DisplayName("값이 존재하지 않으면 런타임 에러 발생")
    void getVideo_whenVideoNotExist_throwException(){
        //given
        Long videoId = 1L;
        when(videoRepository.findById(videoId)).thenReturn(Optional.empty());
        //when
        EntityNotFoundException exception
                = assertThrows(EntityNotFoundException.class, () -> videoComponent.getVideo(videoId));
        //then
        assertEquals("해당 비디오가 존재 하지 않습니다.", exception.getMessage());
        verify(videoRepository, times(1)).findById(videoId);
    }
}