package com.sparta.adjustment.api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VideoStreamingRequest {
    private Long userId;
    private Integer exitTiming;
    private Integer viewingTime;
}
