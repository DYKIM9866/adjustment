package com.sparta.adjustment.domain.video.component;

import com.sparta.adjustment.domain.video.Video;
import com.sparta.adjustment.domain.video.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class VideoComponent {

    private final VideoRepository videoRepository;

    public Optional<Video> getVideo(Long videoId) {
        return videoRepository.findById(videoId);
    }
}
