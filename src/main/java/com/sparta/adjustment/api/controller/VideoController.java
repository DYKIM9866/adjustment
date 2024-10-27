package com.sparta.adjustment.api.controller;

import com.sparta.adjustment.api.dto.request.VideoStreamingRequest;
import com.sparta.adjustment.api.dto.response.VideoStreamingResponse;
import com.sparta.adjustment.usecase.VideoStreamingUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/video")
@RequiredArgsConstructor
public class VideoController {

    private final VideoStreamingUseCase watchVideoUseCase;

    @GetMapping("/{videoId}")
    public ResponseEntity<VideoStreamingResponse> watchVideo(@PathVariable Long videoId
                           , @RequestBody VideoStreamingRequest request){
        watchVideoUseCase.watchVideo(videoId, request);
        return null;
    }

    @PatchMapping("/{videoId}")
    public void finishVideo(@PathVariable Long videoId){

    }
}
