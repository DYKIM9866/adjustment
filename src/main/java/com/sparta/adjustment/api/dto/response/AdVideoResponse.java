package com.sparta.adjustment.api.dto.response;

import com.sparta.adjustment.domain.adVideo.AdVideo;
import lombok.Getter;

@Getter
public class AdVideoResponse {
    private Long adVideoId;
    private String category;
    private String url;
    private Integer adVideoLen;

    public AdVideoResponse(AdVideo adVideo) {
        this.adVideoId = adVideo.getId();
        this.category = adVideo.getCategory();
        this.url = adVideo.getUrl();
        this.adVideoLen = adVideo.getAdVideoLen();
    }
}
