package com.sparta.adjustment.domain.user.component;

import com.sparta.adjustment.api.user.dtos.SocialAuthResponse;
import com.sparta.adjustment.api.user.dtos.SocialUserResponse;
import com.sparta.adjustment.domain.user.enums.SocialType;

public interface SocialLogin {
    SocialType getServiceName();
    SocialAuthResponse getAccessToken(String authCode);
    SocialUserResponse getUserInfo(String accessToken);
}
