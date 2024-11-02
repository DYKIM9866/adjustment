package com.sparta.adjustment.api.dto.response;

import com.sparta.adjustment.domain.video.Video;
import lombok.Builder;
import lombok.Getter;

@Getter
public class VideoStreamingResponse<T> {
    private Integer exitTiming;
    private T video;

    public VideoStreamingResponse(T video) {
        this.video = video;
    }

    public VideoStreamingResponse(Integer exitTiming, T video) {
        this.exitTiming = exitTiming;
        this.video = video;
    }
}
