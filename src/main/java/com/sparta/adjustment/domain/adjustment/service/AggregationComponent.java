package com.sparta.adjustment.domain.adjustment.service;

import com.sparta.adjustment.api.dto.response.AdjustmentCheckResponse;
import com.sparta.adjustment.domain.adjustment.repository.AggregationRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class AggregationComponent {

    private final AggregationRepository aggregationRepository;

    public AggregationComponent(AggregationRepository aggregationRepository) {
        this.aggregationRepository = aggregationRepository;
    }

    public List<AdjustmentCheckResponse> getVideosAggregation(Long userId,
                                                              LocalDateTime startDate,
                                                              LocalDateTime endDate) {
        if(endDate == null){
            return aggregationRepository.getVideosAggregationRecently(userId);
        }else{
            return aggregationRepository.getVideosAggregationBetween(userId, startDate, endDate);
        }
    }

    public List<AdjustmentCheckResponse> getViewsTopVideo(Long userId) {
        return aggregationRepository.getViewsTopVideo(userId);
    }

    public List<AdjustmentCheckResponse> getViewingTimeTopVideo(Long userId) {
        return aggregationRepository.getViewingTimeTopVideo(userId);
    }
}
