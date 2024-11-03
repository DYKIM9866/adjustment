package com.sparta.adjustment.usecase;

import com.sparta.adjustment.api.dto.response.VideoStreamingResponse;
import com.sparta.adjustment.domain.history.UserVideoCheckHistory;
import com.sparta.adjustment.domain.history.UserVideoHistory;
import com.sparta.adjustment.domain.history.UserVideoId;
import com.sparta.adjustment.domain.user.component.UserComponent;
import com.sparta.adjustment.domain.history.component.HistoryComponent;
import com.sparta.adjustment.domain.history.enums.ViewingStatus;
import com.sparta.adjustment.domain.video.Video;
import com.sparta.adjustment.domain.adVideo.component.AdVideoComponent;
import com.sparta.adjustment.domain.video.component.VideoComponent;
import com.sparta.adjustment.domain.video.component.VideoRedisComponent;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VideoStreamingUseCase {
    private final static String WATCHED = "watched";
    private final static String VIEWS = "views";

    private final VideoComponent videoComponent;
    private final VideoRedisComponent videoRedisComponent;
    private final UserComponent userComponent;
    private final HistoryComponent historyComponent;
    private final AdVideoComponent adVideoComponent;


    public VideoStreamingResponse watchVideo(Long videoId, Long userId) {

        Video video = videoComponent.getVideo(videoId);
        //30초 이내 캐시 확인
        Integer watchCached = (Integer) videoRedisComponent.getCached(WATCHED + videoId + userId);

        //게시자 아니고 && 어뷰징 아니라면 조회수 증가
        UserVideoCheckHistory userVideoCheckHistory = null;
        if(!video.getPublisher().equals(userId)
                && watchCached == null){
            videoRedisComponent.increaseCached(VIEWS + videoId);
            videoRedisComponent.increaseCached(WATCHED + videoId + userId);
            userVideoCheckHistory = UserVideoCheckHistory.builder()
                    .userId(userId)
                    .videoId(videoId)
                    .exitTiming(0)
                    .adViews(0)
                    .viewingTime(0)
                    .build();
        }

        //재생 기록이 있는지 조회
        UserVideoId id = new UserVideoId(userId, videoId);
        UserVideoHistory userVideoHistories
                = historyComponent.getUserVideoHistory(id).orElse(null);
        if(userVideoHistories == null){
            userVideoHistories = historyComponent.saveUserVideoHistory(new UserVideoHistory(id, 0));
        }else{
            userVideoHistories.setExitTiming(userVideoHistories.getExitTiming() - 1);
        }

        //check 기록 있다면 저장
        if(userVideoCheckHistory != null){
            historyComponent.saveCheckHistory(userVideoCheckHistory);
        }

        return new VideoStreamingResponse(userVideoHistories.getExitTiming(), video);
    }

//    public VideoStreamingResponse<AdVideo> watchAd(Long videoId, Long userId, Integer adVideoLen) {
//        AdVideo adVideo = adVideoComponent.getAdVideo(adVideoLen);
//        UserVideoHistory userVideoHistory
//                = historyComponent.getUserVideoHistory(userId, videoId)
//                .orElseThrow(() -> new RuntimeException("기록이 조회되지 않습니다."));
//
//        userVideoHistory.setAdViews(userVideoHistory.getAdViews() + 1);
//
//        return new VideoStreamingResponse<>(adVideo);
//    }

    public void finishVideo(Long videoId, Long userId, Integer exitTiming) {
        UserVideoId userVideoId = new UserVideoId(userId, videoId);
        UserVideoHistory userVideoHistory
                = historyComponent.getUserVideoHistory(userVideoId)
                .orElseThrow(()-> new EntityNotFoundException("기록이 조회되지 않아 기록에 실패했습니다."));


        videoRedisComponent.setCached(WATCHED + videoId + userId, 1, 30);
    }
}
