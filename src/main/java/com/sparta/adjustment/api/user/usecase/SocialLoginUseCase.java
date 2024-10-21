package com.sparta.adjustment.api.user.usecase;

import com.sparta.adjustment.api.user.dtos.LoginResponse;
import com.sparta.adjustment.api.user.dtos.SocialAuthResponse;
import com.sparta.adjustment.api.user.dtos.SocialLoginRequest;
import com.sparta.adjustment.api.user.dtos.SocialUserResponse;
import com.sparta.adjustment.domain.user.component.SocialLogin;
import com.sparta.adjustment.domain.user.component.UserLogin;
import com.sparta.adjustment.domain.user.enums.SocialType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class SocialLoginUseCase {

    private final UserLogin userLogin;

    public LoginResponse socialLogin(SocialLoginRequest request) {
        //User정보 가져오기
        SocialUserResponse userInfo = userLogin.getUserInfo(request);

        log.info("User Info {}", userInfo.toString());

        //비어있다면
        if(userLogin.isExist(userInfo.getEmail())){
            userLogin.signUp(userInfo);
        }


        return null;
    }


}
