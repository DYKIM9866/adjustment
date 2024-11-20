package com.sparta.adjustment.domain.user.component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sparta.adjustment.api.dto.response.NaverLoginResponse;
import com.sparta.adjustment.api.dto.response.SocialAuthResponse;
import com.sparta.adjustment.api.dto.response.SocialUserResponse;
import com.sparta.adjustment.client.feign.naver.NaverAuthApi;
import com.sparta.adjustment.client.feign.naver.NaverUserApi;
import com.sparta.adjustment.domain.user.enums.SocialType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class NaverLoginService implements SocialLogin{

    private final NaverAuthApi naverAuthApi;
    private final NaverUserApi naverUserApi;

    private final Gson gson;


    @Value("#{social['naver.client_id']}")
    private String naverClientId;
    @Value("#{social['naver.login_url']}")
    private String naverLoginUrl;
    @Value("#{social['naver.redirect-uri']}")
    private String naverRedirectUri;
    @Value("#{social['naver.secret_key']}")
    private String naverSecretKey;

    @Override
    public String getLoginPage() {
        String currentDateTime = LocalDateTime.now().toString();
        
        return naverLoginUrl + "?response_type=code" +
                "&client_id=" + naverClientId +
                "&redirect_uri=" + naverRedirectUri +
                "$state=" + URLEncoder.encode(currentDateTime, StandardCharsets.UTF_8);
    }

    @Override
    public SocialType getServiceName() {
        return SocialType.NAVER;
    }

    @Override
    public SocialAuthResponse getAccessToken(String authCode) {
        String currentDateTime = LocalDateTime.now().toString();
        String decode = URLDecoder.decode(authCode, StandardCharsets.UTF_8);

        ResponseEntity<String> response = naverAuthApi.getAccessToken(
                "authorization_code",
                naverClientId,
                naverSecretKey,
                decode,
                URLEncoder.encode(currentDateTime, StandardCharsets.UTF_8)
        );

        log.info("naver auth info");
        log.info(response.toString());

        return gson.fromJson(
                response.getBody().toString(),
                SocialAuthResponse.class
        );
    }

    @Override
    public SocialUserResponse getUserInfo(String accessToken) {
        HashMap<String, String> header = new HashMap<>();
        header.put("authorization", "Bearer " + accessToken);

        ResponseEntity<String> response = naverUserApi.getUserInfo(header);

        log.info("naver user info");
        log.info(response.toString());

        String jsonString = response.getBody();

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        NaverLoginResponse naverLoginResponse = gson.fromJson(jsonString, NaverLoginResponse.class);
        NaverLoginResponse.Response userInfo = naverLoginResponse.getResponse();

        return SocialUserResponse.builder()
                .id(userInfo.getId())
                .email(userInfo.getEmail())
                .build();
    }
}
