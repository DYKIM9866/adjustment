package com.sparta.adjustment.domain.user.component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sparta.adjustment.api.user.dtos.GoogleLoginResponse;
import com.sparta.adjustment.api.user.dtos.GoogleRequestAccessToken;
import com.sparta.adjustment.api.user.dtos.SocialAuthResponse;
import com.sparta.adjustment.api.user.dtos.SocialUserResponse;
import com.sparta.adjustment.domain.user.enums.SocialType;
import com.sparta.adjustment.domain.user.feign.google.GoogleAuthApi;
import com.sparta.adjustment.domain.user.feign.google.GoogleUserApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class GoogleLogin implements SocialLogin{

    private final GoogleAuthApi googleAuthApi;
    private final GoogleUserApi googleUserApi;
    private final Gson gson;

    @Value("#{social['google.rest-api-key']}")
    private String googleAppKey;
    @Value("#{social['google.secret-key']}")
    private String googleAppSecret;
    @Value("#{social['google.redirect-uri']}")
    private String googleRedirectUri;
    @Value("#{social['google.grant_type']}")
    private String googleGrantType;

    @Override
    public SocialAuthResponse getAccessToken(String authCode) {
        String decode = URLDecoder.decode(authCode, StandardCharsets.UTF_8);
        ResponseEntity<?> response = googleAuthApi.getAccessToken(
                GoogleRequestAccessToken.builder()
                        .code(decode)
                        .client_id(googleAppKey)
                        .clientSecret(googleAppSecret)
                        .redirect_uri(googleRedirectUri)
                        .grant_type(googleGrantType)
                        .build()
        );

        log.info("google auth info");
        log.info(response.toString());

        return gson.fromJson(
                response.getBody().toString(),
                SocialAuthResponse.class);
    }

    @Override
    public SocialUserResponse getUserInfo(String accessToken) {
        ResponseEntity<String> userInfo = googleUserApi.getUserInfo(accessToken);

        String jsonString = userInfo.getBody().toString();

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        GoogleLoginResponse googleLoginResponse = gson.fromJson(jsonString, GoogleLoginResponse.class);

        return SocialUserResponse.builder()
                .id(googleLoginResponse.getId())
                .email(googleLoginResponse.getEmail())
                .build();
    }

    @Override
    public SocialType getServiceName() {
        return SocialType.GOOGLE;
    }
}