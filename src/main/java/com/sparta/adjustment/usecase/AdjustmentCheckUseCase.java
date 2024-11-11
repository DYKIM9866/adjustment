package com.sparta.adjustment.usecase;

import com.sparta.adjustment.api.dto.response.AdjustmentCheckResponse;
import com.sparta.adjustment.domain.adjustment.Aggregation;
import com.sparta.adjustment.domain.adjustment.service.AggregationComponent;
import com.sparta.adjustment.domain.video.Video;
import com.sparta.adjustment.domain.video.component.VideoComponent;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class AdjustmentCheckUseCase {

    private final AggregationComponent aggregationComponent;

    public AdjustmentCheckUseCase(AggregationComponent aggregationComponent) {
        this.aggregationComponent = aggregationComponent;
    }

    public List<AdjustmentCheckResponse> getAggregationList(Long userId,
                                                            LocalDateTime startDate,
                                                            LocalDateTime endDate) {
        return aggregationComponent.getVideosAggregation(userId, startDate, endDate);
    }


    public List<AdjustmentCheckResponse> getViewsTopVideo(Long userId) {

        return aggregationComponent.getViewsTopVideo(userId);
    }

    public List<AdjustmentCheckResponse> getViewingTimeTopVideo(Long userId) {
        return aggregationComponent.getViewingTimeTopVideo(userId);
    }
}
