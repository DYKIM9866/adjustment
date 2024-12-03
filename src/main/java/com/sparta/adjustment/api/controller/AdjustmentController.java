package com.sparta.adjustment.api.controller;

import com.sparta.adjustment.api.dto.response.AdjustmentCheckResponse;
import com.sparta.adjustment.api.utils.CommonApiResponse;
import com.sparta.adjustment.usecase.AdjustmentCheckUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/adjustment")
@RequiredArgsConstructor
public class AdjustmentController {

    private final AdjustmentCheckUseCase adjustmentCheckUseCase;


    @GetMapping()
    public CommonApiResponse<List<AdjustmentCheckResponse>> getAggregationList
            (@RequestParam Long userId,
             @RequestParam(required = false)
             @DateTimeFormat(pattern = "yyyyMMddHHmmss") LocalDateTime startDate,
             @RequestParam(required = false)
             @DateTimeFormat(pattern = "yyyyMMddHHmmss") LocalDateTime endDate){

        return CommonApiResponse.success(
                adjustmentCheckUseCase.getAggregationList(userId, startDate, endDate));
    }

    @GetMapping("/viewsTop")
    public CommonApiResponse<List<AdjustmentCheckResponse>> getViewsTopVideo(
            @RequestParam Long userId
    ){
        return CommonApiResponse.success(
                adjustmentCheckUseCase.getViewsTopVideo(userId));
    }

    @GetMapping("/viewingTimeTop")
    public CommonApiResponse<List<AdjustmentCheckResponse>> getViewingTimeTopVideo(
            @RequestParam Long userId
    ){
        return CommonApiResponse.success(
                adjustmentCheckUseCase.getViewingTimeTopVideo(userId));
    }
}
