package com.sparta.adjustment.usecase;

import com.sparta.adjustment.api.dto.response.LoginResponse;
import com.sparta.adjustment.api.dto.request.SocialLoginRequest;
import com.sparta.adjustment.api.dto.response.SocialUserResponse;
import com.sparta.adjustment.domain.user.component.UserLogin;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
