package com.sparta.adjustment.domain.adVideo.component;

import com.sparta.adjustment.domain.adVideo.AdVideo;
import com.sparta.adjustment.domain.adVideo.repository.AdVideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdVideoComponent {

    private final AdVideoRepository adVideoRepository;

    public AdVideo getAdVideoByCategory(String category) {
        return adVideoRepository.findRandomOneByCategory(category);
    }
}
