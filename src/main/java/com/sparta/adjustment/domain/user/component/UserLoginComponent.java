package com.sparta.adjustment.domain.user.component;

import com.sparta.adjustment.api.dto.response.SocialAuthResponse;
import com.sparta.adjustment.api.dto.request.SocialLoginRequest;
import com.sparta.adjustment.api.dto.response.SocialUserResponse;
import com.sparta.adjustment.domain.user.User;
import com.sparta.adjustment.domain.user.enums.SocialType;
import com.sparta.adjustment.domain.user.enums.UserAuth;
import com.sparta.adjustment.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserLoginComponent {

    private final List<SocialLogin> socialLogins;
    private final UserRepository userRepository;
    private final UserComponent userComponent;

    public void getAuthorizeLogin(SocialLoginRequest request) {
        //소셜 로그인 별 클래스 가져오기
        SocialLogin socialLogin = getSocial(request.getSocialType())
                .orElseThrow(()-> new NullPointerException("소셜 로그인/가입만을 허용하고 있습니다."));

        socialLogin.getAccessToken(request.getCode());
    }

    public SocialUserResponse getUserInfo(SocialLoginRequest request) {
        //소셜 로그인 별 클래스 가져오기
        SocialLogin socialLogin = getSocial(request.getSocialType())
                .orElseThrow(()-> new NullPointerException("소셜 로그인/가입만을 허용하고 있습니다."));

        //인가코드
        SocialAuthResponse socialAuthResponse = socialLogin.getAccessToken(request.getCode());

        SocialUserResponse userInfo = socialLogin.getUserInfo(socialAuthResponse.getAccess_token());

        return userInfo;
    }

    private Optional<SocialLogin> getSocial(SocialType socialType) {
        for(SocialLogin login : socialLogins){
            if(socialType.equals(login.getServiceName())){
                log.info("{}", login.getServiceName());
                return Optional.of(login);
            }
        }
        return null;
    }

    public User getUser(String email) {
        return userComponent.getUser(email);
    }

    public void signUp(SocialUserResponse userInfo, SocialLoginRequest request) {
        User user = new User(userInfo.getEmail(), UserAuth.NORMAL, request.getSocialType());
        userRepository.save(user);
    }

    public String getSocialLogin(SocialType socialType) {
        SocialLogin social = getSocial(socialType)
                .orElseThrow(()-> new NullPointerException("소셜 로그인/가입만을 허용하고 있습니다."));

        return social.getLogin();
    }
}
