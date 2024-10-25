package com.sparta.adjustment.usecase;

import com.sparta.adjustment.api.dto.request.VideoStreamingRequest;
import com.sparta.adjustment.domain.video.Video;
import com.sparta.adjustment.domain.video.component.VideoComponent;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VideoStreamingUseCase {

    private final VideoComponent videoComponent;
    public void watchVideo(Long videoId, VideoStreamingRequest request) {
        //비디오 객체 가져오기
        Video video = videoComponent.getVideo(videoId)
                .orElseThrow(()-> new EntityNotFoundException("해당 비디오가 존재 하지 않습니다."));

        //게시자 아니고 && 어뷰징 아니라면 조회수 증가
        if(!video.getPublisher().equals(request.getUserEmail())
                && videoComponent.isWatchCachedEmpty(request.getUserEmail(), videoId)){

        }

    }
}
