package com.sparta.adjustment.domain.user.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {
    private static final String AUTH_METHOD = "Bearer";
    private final JwtUtil jwtUtil;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        String token = getTokenToRequest((HttpServletRequest)request);

        if(token != null && jwtUtil.validateToken(token)){
            Authentication authentication = jwtUtil.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenToRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if(StringUtils.hasText(token) && token.startsWith(AUTH_METHOD)){
            return token.substring(7);
        }
        return null;
    }
}
