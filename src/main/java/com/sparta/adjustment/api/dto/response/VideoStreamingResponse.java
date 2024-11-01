package com.sparta.adjustment.api.dto.response;

import com.sparta.adjustment.domain.video.Video;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VideoStreamingResponse {
    private Integer exitTiming;
    private Video video;
}
