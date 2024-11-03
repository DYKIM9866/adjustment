package com.sparta.adjustment.api.dto.response;

import com.sparta.adjustment.domain.video.Video;
import lombok.Builder;
import lombok.Getter;

@Getter
public class VideoStreamingResponse {
    private Integer exitTiming;
    private Video video;

    public VideoStreamingResponse(Integer exitTiming, Video video) {
        this.exitTiming = exitTiming;
        this.video = video;
    }
}
