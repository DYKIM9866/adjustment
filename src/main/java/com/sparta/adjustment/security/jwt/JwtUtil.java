package com.sparta.adjustment.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class JwtUtil {

    private Key secretKey;

    public JwtUtil(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public Authentication getAuthentication(String accessToken) {
        //토큰 복호화
        Claims claims = parseClaims(accessToken);
        
        if(claims.get("auth") == null){
            throw new RuntimeException("권한 정보가 없습니다.");
        }

        List<? extends GrantedAuthority> auth = Arrays.stream(claims.get("auth").toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .toList();

        UserDetails userDetails = new User(claims.getSubject(), "", auth);

        return new UsernamePasswordAuthenticationToken(userDetails,"",auth);
    }

    public boolean validateToken(String token){
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);

            return true;
        }catch (SecurityException | MalformedJwtException e){
            log.debug("Invalid JWT Token", e);
        }catch (ExpiredJwtException e){
            log.debug("Expired JWT Token", e);
        }catch (UnsupportedJwtException e){
            log.debug("Unsupported JWT Token", e);
        }catch (IllegalArgumentException e){
            log.debug("JWT claims string is empty", e);
        }
        return false;
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        }catch (ExpiredJwtException e){
            return e.getClaims();
        }
    }

}
