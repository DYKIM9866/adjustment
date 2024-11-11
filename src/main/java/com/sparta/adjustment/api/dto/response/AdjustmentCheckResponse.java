package com.sparta.adjustment.api.dto.response;

public interface AdjustmentCheckResponse {

    Long getUserId();
    Long getVideoId();
    String getTitle();
    String getUrl();
    Long getTotalAdAmount();
    Long getTotalViewsAmount();
    Long getViewingTime();
    Long getViews();
}
