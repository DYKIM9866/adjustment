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
public class UserLogin {

    private final List<SocialLogin> socialLogins;
    private final UserRepository userRepository;

    public SocialUserResponse getUserInfo(SocialLoginRequest request) {
        SocialLogin socialLogin = getLogin(request.getSocialType())
                .orElseThrow(()-> new IllegalArgumentException("소셜 로그인/가입만을 허용하고 있습니다."));

        SocialAuthResponse socialAuthResponse = socialLogin.getAccessToken(request.getCode());
        SocialUserResponse userInfo = socialLogin.getUserInfo(socialAuthResponse.getAccess_token());

        return userInfo;
    }

    private Optional<SocialLogin> getLogin(SocialType socialType) {
        for(SocialLogin login : socialLogins){
            if(socialType.equals(login.getServiceName())){
                log.info("{}", login.getServiceName());
                return Optional.of(login);
            }
        }
        return null;
    }

    public User getUser(String email) {
        return userRepository.findByEmail(email);
    }
    public boolean isExist(String email) {
        User user = userRepository.findByEmail(email);
        return user == null;
    }

    public void signUp(SocialUserResponse userInfo, SocialLoginRequest request) {
        User user = new User(userInfo.getEmail(), UserAuth.NORMAL, request.getSocialType());
        userRepository.save(user);
    }
}
