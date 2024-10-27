package com.sparta.adjustment.security.jwt;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class JwtToken {
    private String grantType;
    private String accessToken;
    private String refreshToken;
}
