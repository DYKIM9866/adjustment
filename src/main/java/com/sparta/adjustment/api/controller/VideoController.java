package com.sparta.adjustment.api.controller;

import com.sparta.adjustment.api.dto.response.VideoStreamingResponse;
import com.sparta.adjustment.api.utils.CommonApiResponse;
import com.sparta.adjustment.usecase.VideoStreamingUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/video")
@RequiredArgsConstructor
public class VideoController {

    private final VideoStreamingUseCase videoStreamingUseCase;

    @GetMapping("/{videoId}/{userId}")
    public CommonApiResponse<VideoStreamingResponse<?>> watchVideo(@PathVariable Long videoId,
                                                                       @PathVariable Long userId){
        return CommonApiResponse.success(videoStreamingUseCase.watchVideo(videoId, userId));
    }

    @PatchMapping("/{videoId}/{userId}/{adVideoLen}")
    public CommonApiResponse<VideoStreamingResponse<?>> watchAd(@PathVariable Long videoId,
                                        @PathVariable Long userId,
                                        @PathVariable Integer adVideoLen){
        return CommonApiResponse.success(videoStreamingUseCase.watchAd(videoId, userId, adVideoLen));
    }

    @PatchMapping("/{videoId}/{userId}")
    public void finishVideo(@PathVariable Long videoId,
                            @PathVariable Long userId,
                            @RequestParam Integer exitTiming){
        videoStreamingUseCase.finishVideo(videoId, userId, exitTiming);
    }
}
