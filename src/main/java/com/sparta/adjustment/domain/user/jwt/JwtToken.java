package com.sparta.adjustment.domain.user.jwt;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class JwtToken {
    private String grantType;
    private String accessToken;
    private String refreshToken;
}
