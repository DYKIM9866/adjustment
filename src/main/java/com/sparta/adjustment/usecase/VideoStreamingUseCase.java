package com.sparta.adjustment.usecase;

import com.sparta.adjustment.api.dto.response.VideoStreamingResponse;
import com.sparta.adjustment.domain.user.User;
import com.sparta.adjustment.domain.user.UserVideoHistory;
import com.sparta.adjustment.domain.user.component.UserComponent;
import com.sparta.adjustment.domain.user.component.UserHistoryComponent;
import com.sparta.adjustment.domain.user.enums.ViewingStatus;
import com.sparta.adjustment.domain.video.AdVideo;
import com.sparta.adjustment.domain.video.Video;
import com.sparta.adjustment.domain.video.component.AdVideoComponent;
import com.sparta.adjustment.domain.video.component.VideoComponent;
import com.sparta.adjustment.domain.video.component.VideoRedisComponent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class VideoStreamingUseCase {

    private final VideoComponent videoComponent;
    private final VideoRedisComponent videoRedisComponent;
    private final UserComponent userComponent;
    private final UserHistoryComponent userHistoryComponent;
    private final AdVideoComponent adVideoComponent;


    public VideoStreamingResponse<Video> watchVideo(Long videoId, Long userId) {

        User user = userComponent.getUser(userId);
        Video video = videoComponent.getVideo(videoId);
        //30초 이내 캐시 확인
        Integer watchCached = videoRedisComponent.getWatchCached(userId, videoId);

        //게시자 아니고 && 어뷰징 아니라면 조회수 증가
        boolean isViews = false;
        if(!video.getPublisher().equals(userId)
                && watchCached == null){
            videoRedisComponent.increaseWatched(videoId);
            isViews = true;
        }

        //재생 기록이 있는지 조회
        Optional<UserVideoHistory> userVideoHistories
                = userHistoryComponent.getUserVideoHistory(userId, videoId);

        //기록 생성 및 저장
        UserVideoHistory userVideoHistory = UserVideoHistory.builder()
                .userId(userId)
                .videoId(videoId)
                .views(isViews)
                .viewingStatus(ViewingStatus.WATCHING)
                .build();

        userHistoryComponent.saveUserVideoHistory(userVideoHistory);

        return new VideoStreamingResponse<>(video);
    }

    public VideoStreamingResponse<AdVideo> watchAd(Long videoId, Long userId, Integer adVideoLen) {
        AdVideo adVideo = adVideoComponent.getAdVideo(adVideoLen);
        UserVideoHistory userVideoHistory
                = userHistoryComponent.getUserVideoHistory(userId, videoId)
                .orElseThrow(() -> new RuntimeException("기록이 조회되지 않습니다."));

        userVideoHistory.setAdViews(userVideoHistory.getAdViews() + 1);

        return new VideoStreamingResponse<>(adVideo);
    }
}
