package com.sparta.adjustment.usecase;

import com.sparta.adjustment.api.dto.request.VideoStreamingRequest;
import com.sparta.adjustment.api.dto.response.AdVideoResponse;
import com.sparta.adjustment.api.dto.response.VideoStreamingResponse;
import com.sparta.adjustment.domain.adVideo.component.AdVideoComponent;
import com.sparta.adjustment.domain.history.UserVideoCheckHistory;
import com.sparta.adjustment.domain.history.UserVideoHistory;
import com.sparta.adjustment.domain.history.UserVideoId;
import com.sparta.adjustment.domain.history.component.HistoryComponent;
import com.sparta.adjustment.domain.video.Video;
import com.sparta.adjustment.domain.video.component.VideoComponent;
import com.sparta.adjustment.domain.video.component.VideoRedisComponent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class VideoStreamingUseCase {
    private final static String WATCHED = "watched";
    private final static String VIEWS = "views";
    private final static String VIDEO_LOG = "videoLog";

    private final VideoComponent videoComponent;
    private final VideoRedisComponent videoRedisComponent;
    private final HistoryComponent historyComponent;
    private final AdVideoComponent adVideoComponent;


    public VideoStreamingResponse watchVideo(Long videoId, Long userId) {

        Video video = videoComponent.getVideo(videoId);

        //video log남기기
        if(videoRedisComponent.getCached(VIDEO_LOG + ":" + videoId) == null){
            videoRedisComponent.increaseCached(VIDEO_LOG + ":" + videoId);
            videoComponent.saveDayLog(videoId);
        }

        //30초 이내 캐시 확인
        Object watchCached = videoRedisComponent.getCached(WATCHED + videoId + userId);

        //게시자 아니고 && 어뷰징 아니라면 조회수 증가
        UserVideoCheckHistory userVideoCheckHistory = null;
        if(!video.getPublisher().equals(userId)
                && watchCached == null){
            videoRedisComponent.increaseCached(VIEWS + videoId);
            videoRedisComponent.increaseCached(WATCHED + videoId + userId);
            userVideoCheckHistory = UserVideoCheckHistory.builder()
                    .userId(userId)
                    .videoId(videoId)
                    .exitTiming(0L)
                    .adViews(0L)
                    .viewingTime(0L)
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
    @Transactional
    public void finishVideo(Long videoId, VideoStreamingRequest request) {
        UserVideoCheckHistory checkHistory
                = historyComponent.getUserVideoCheckHistory(videoId, request.getUserId());
        UserVideoHistory userVideoHistory
                = historyComponent.getUserVideoHistory(new UserVideoId(request.getUserId(), videoId))
                    .orElse(null);
        checkHistory.setViewingTime(Long.valueOf(request.getViewingTime()));
        checkHistory.setExitTiming(Long.valueOf(request.getExitTiming()));
        if(userVideoHistory != null){
            userVideoHistory.setExitTiming(request.getExitTiming());
        }

        Object cached = videoRedisComponent.getCached(WATCHED + videoId + request.getUserId());
        if(cached == null){
            videoRedisComponent.setCached(WATCHED + videoId + request.getUserId(), 1, 30);
        }else{
            videoRedisComponent.decreaseCache(WATCHED + videoId + request.getUserId());
        }

    }
    public AdVideoResponse getAdVideo(String category) {
        return new AdVideoResponse(adVideoComponent.getAdVideoByCategory(category));
    }
    public void finishAdVideo(Long videoId, VideoStreamingRequest request) {
        UserVideoCheckHistory checkHistory
                = historyComponent.getUserVideoCheckHistory(videoId, request.getUserId());
        checkHistory.setAdViews(checkHistory.getAdViews()+1);
    }
}
