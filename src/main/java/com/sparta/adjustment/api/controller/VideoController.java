package com.sparta.adjustment.api.controller;

import com.sparta.adjustment.api.dto.request.VideoStreamingRequest;
import com.sparta.adjustment.api.dto.response.AdVideoResponse;
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

    @GetMapping("/{videoId}")
    public CommonApiResponse<VideoStreamingResponse> getVideo(@PathVariable Long videoId,
                                                              @RequestBody VideoStreamingRequest request){
        return CommonApiResponse.success(videoStreamingUseCase.watchVideo(videoId, request.getUserId()));
    }
    @PatchMapping("/{videoId}")
    public void finishVideo(@PathVariable Long videoId,
                            @RequestBody VideoStreamingRequest request){
        videoStreamingUseCase.finishVideo(videoId, request);
    }

    @GetMapping("/ad")
    public CommonApiResponse<AdVideoResponse> getAdVideo(@RequestParam String category){
        return CommonApiResponse.success(videoStreamingUseCase.getAdVideo(category));
    }
    @PatchMapping("/ad/{videoId}/{userId}")
    public void finishAdVideo(@PathVariable Long videoId,
                              @RequestBody VideoStreamingRequest request){
        videoStreamingUseCase.finishAdVideo(videoId, request);
    }

//    @PatchMapping("/{videoId}/{userId}/{adVideoLen}")
//    public CommonApiResponse<VideoStreamingResponse<?>> watchAd(@PathVariable Long videoId,
//                                        @PathVariable Long userId,
//                                        @PathVariable Integer adVideoLen){
//        return CommonApiResponse.success(videoStreamingUseCase.watchAd(videoId, userId, adVideoLen));
//    }


}
