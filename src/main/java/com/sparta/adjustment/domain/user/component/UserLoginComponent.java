package com.sparta.adjustment.domain.user.component;

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

    public String getSocialLogin(SocialType socialType) {
        SocialLogin social = getSocialLoginService(socialType)
                .orElseThrow(()-> new NullPointerException("소셜 로그인/가입만을 허용하고 있습니다."));

        return social.getLogin();
    }

    public Optional<SocialLogin> getSocialLoginService(SocialType socialType) {
        for(SocialLogin login : socialLogins){
            if(socialType.equals(login.getServiceName())){
                log.info("{}", login.getServiceName());
                return Optional.of(login);
            }
        }
        return null;
    }
}
