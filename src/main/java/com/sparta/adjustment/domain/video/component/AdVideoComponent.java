package com.sparta.adjustment.domain.video.component;

import com.sparta.adjustment.domain.video.AdVideo;
import com.sparta.adjustment.domain.video.repository.AdVideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdVideoComponent {

    private final AdVideoRepository adVideoRepository;

    public AdVideo getAdVideo(Integer adVideoLen) {
        return adVideoRepository.findRandomOneByLength(adVideoLen);
    }
}
