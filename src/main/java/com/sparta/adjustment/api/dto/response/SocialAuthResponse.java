package com.sparta.adjustment.api.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SocialAuthResponse {
    private String access_token;
    private String token_type;
    private String refresh_token;
    private String expires_in;
    private String scope;
    private String refresh_token_expires_in;
}
