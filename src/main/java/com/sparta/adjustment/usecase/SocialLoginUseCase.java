package com.sparta.adjustment.usecase;

import com.sparta.adjustment.api.dto.request.SocialLoginRequest;
import com.sparta.adjustment.api.dto.response.LoginResponse;
import com.sparta.adjustment.api.dto.response.SocialAuthResponse;
import com.sparta.adjustment.api.dto.response.SocialUserResponse;
import com.sparta.adjustment.domain.user.User;
import com.sparta.adjustment.domain.user.component.SocialLogin;
import com.sparta.adjustment.domain.user.component.UserComponent;
import com.sparta.adjustment.domain.user.component.UserLoginComponent;
import com.sparta.adjustment.domain.user.enums.SocialType;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SocialLoginUseCase {

    private final UserComponent userComponent;
    private final UserLoginComponent userLoginComponent;


    public LoginResponse socialLogin(SocialLoginRequest request) {

        //request의 로그인 service 가져오기
        SocialLogin loginService = userLoginComponent.getSocialLoginService(request.getSocialType())
                .orElseThrow(()->new NullPointerException("소셜 로그인/가입만을 허용하고 있습니다."));

        // 로그인 토큰 가져오기
        SocialAuthResponse socialAuthResponse = loginService.getAccessToken(request.getCode());
        // 유저정보 가져오기
        SocialUserResponse socialUserResponse
                = loginService.getUserInfo(socialAuthResponse.getAccess_token());

        if(userComponent.getUser(socialUserResponse.getEmail()).isEmpty()){
            userComponent.joinUser(socialUserResponse, request.getSocialType());
        }

        User user = userComponent.getUser(socialUserResponse.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("유저 정보를 찾을 수 없습니다."));

        return new LoginResponse(user.getId());
    }


    public String getLoginPage(SocialType socialType) {
        String socialLogin = userLoginComponent.getSocialLogin(socialType);
        log.info(socialLogin);
        return socialLogin;
    }

//    public String getAuthorizeLogin(SocialLoginRequest request) {
//    }
}
