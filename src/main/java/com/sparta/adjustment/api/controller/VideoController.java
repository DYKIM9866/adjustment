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

    private final VideoStreamingUseCase watchVideoUseCase;

    @GetMapping("/{videoId}/{userId}")
    public CommonApiResponse<VideoStreamingResponse> watchVideo(@PathVariable Long videoId
                           , @PathVariable Long userId){
        return CommonApiResponse.success(watchVideoUseCase.watchVideo(videoId, userId));
    }

    @PatchMapping("/{videoId}")
    public void finishVideo(@PathVariable Long videoId){

    }
}
