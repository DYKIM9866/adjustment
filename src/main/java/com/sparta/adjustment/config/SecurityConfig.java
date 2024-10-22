package com.sparta.adjustment.config;

import com.sparta.adjustment.domain.user.jwt.JwtAuthenticationFilter;
import com.sparta.adjustment.domain.user.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
//        return httpSecurity
//                .httpBasic().disable()
//                .csrf().disable()
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .and()
//                .authorizeHttpRequests()
//                .requestMatchers("/api/user/social-login").permitAll()
//                .anyRequest().authenticated()
//                .and()
//                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil),
//                        UsernamePasswordAuthenticationFilter.class)
//                .build();
//    }
}
