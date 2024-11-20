package com.sparta.adjustment.domain.user.component;

import com.sparta.adjustment.api.dto.response.SocialAuthResponse;
import com.sparta.adjustment.api.dto.response.SocialUserResponse;
import com.sparta.adjustment.domain.user.enums.SocialType;

public interface SocialLogin {
    String getLoginPage();
    SocialType getServiceName();
    SocialAuthResponse getAccessToken(String authCode);
    SocialUserResponse getUserInfo(String accessToken);
}
