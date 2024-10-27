package com.sparta.adjustment.usecase;

import com.sparta.adjustment.api.dto.request.VideoStreamingRequest;
import com.sparta.adjustment.domain.user.component.UserHistoryComponent;
import com.sparta.adjustment.domain.video.Video;
import com.sparta.adjustment.domain.video.component.VideoComponent;
import com.sparta.adjustment.domain.video.component.VideoRedisComponent;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VideoStreamingUseCase {

    private final VideoComponent videoComponent;
    private final VideoRedisComponent videoRedisComponent;
    private final UserHistoryComponent userHistoryComponent;

    public void watchVideo(Long videoId, VideoStreamingRequest request) {
        //비디오 객체 가져오기
        Video video = videoComponent.getVideo(videoId)
                .orElseThrow(()-> new EntityNotFoundException("해당 비디오가 존재 하지 않습니다."));
        //30초 이내 캐시 확인
        Integer watchCached = videoRedisComponent.getWatchCached(request.getUserEmail(), videoId);

        //게시자 아니고 && 어뷰징 아니라면 조회수 증가
        if(!video.getPublisher().equals(request.getUserEmail())
                && watchCached == null){
            videoRedisComponent.increaseWatched(videoId);
        }

        //재생 기록이 있는지 조회
        /**
         * 관계를 맺어둔 엔티티에서 조회해서 로직상에서 찾아서 하는게 맞는지
         * 쿼리를 한번 더 던지는게 맞는지
         */

        //기록 생성 후 비디오 리턴
        return;
    }
}
