package com.sparta.adjustment.domain.video.component;

import com.sparta.adjustment.domain.video.Video;
import com.sparta.adjustment.domain.video.repository.VideoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VideoComponent {

    private final VideoRepository videoRepository;

    public Video getVideo(Long videoId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(()->new EntityNotFoundException("해당 비디오가 존재 하지 않습니다."));

        return video;
    }
}
