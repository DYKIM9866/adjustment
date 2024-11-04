package com.sparta.adjustment.api.dto.response;

import com.sparta.adjustment.domain.video.Video;
import lombok.Getter;

@Getter
public class VideoStreamingResponse {
    private Long videoId;
    private Long publisher;
    private String title;
    private Integer videoLen;
    private String videoExplain;
    private String url;

    private Integer exitTiming;

    public VideoStreamingResponse(Integer exitTiming, Video video) {
        this.exitTiming = exitTiming;
        this.videoId = video.getId();
        this.publisher = video.getPublisher();
        this.title = video.getTitle();
        this.videoLen = video.getVideoLen();
        this.videoExplain = video.getVideoExplain();
        this.url = video.getUrl();
    }
}
