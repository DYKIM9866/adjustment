package com.sparta.adjustment.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenGenerate {

    private final static long ACCESS_TOKEN_EXPIRE_TIME = 1_800;
    private final static long REFRESH_TOKEN_EXPIRE_TIME = 2_592_000;

    private Key secretKey;

    public JwtTokenGenerate(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public JwtToken generateToken(Long userId){

        long now = new Date().getTime();

        //AccessToken 발급
        String accessToken = Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setExpiration(new Date(now + ACCESS_TOKEN_EXPIRE_TIME))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        //RefreshToken 발급
        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        return new JwtToken("Bearer", accessToken, refreshToken);
    }
}
