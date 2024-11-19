package com.sparta.adjustment.usecase;

import com.sparta.adjustment.api.dto.response.LoginResponse;
import com.sparta.adjustment.api.dto.request.SocialLoginRequest;
import com.sparta.adjustment.api.dto.response.SocialUserResponse;
import com.sparta.adjustment.domain.user.User;
import com.sparta.adjustment.domain.user.component.UserLoginComponent;
import com.sparta.adjustment.domain.user.enums.SocialType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SocialLoginUseCase {

    private final UserLoginComponent userLogin;

    public LoginResponse socialLogin(SocialLoginRequest request) {
        //소셜 로그인에서 User정보 가져오기
        SocialUserResponse userInfo = userLogin.getUserInfo(request);

        log.info("User Info {}", userInfo.toString());

        //조회
        User user = userLogin.getUser(userInfo.getEmail());
        if(user == null){
            userLogin.signUp(userInfo, request);
        }

        user = userLogin.getUser(userInfo.getEmail());




        return null;
    }


    public String getLogin(SocialType socialType) {
        String socialLogin = userLogin.getSocialLogin(socialType);
        log.info(socialLogin);
        return socialLogin;
    }

//    public String getAuthorizeLogin(SocialLoginRequest request) {
//    }
}
