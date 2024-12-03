package com.sparta.adjustment.api.controller;

import com.sparta.adjustment.api.dto.request.SocialLoginRequest;
import com.sparta.adjustment.api.dto.response.LoginResponse;
import com.sparta.adjustment.domain.user.enums.SocialType;
import com.sparta.adjustment.usecase.SocialLoginUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final SocialLoginUseCase socialUsecase;

    @GetMapping("/auth/login")
    public ResponseEntity<?> redirectToSocialLogin(@RequestParam String socialType){
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", socialUsecase.getLoginPage(SocialType.valueOf(socialType))).build();
    }

    @PostMapping("/social-login")
    public ResponseEntity<LoginResponse> socialLogin(@RequestBody @Valid SocialLoginRequest request){
        return ResponseEntity.created(URI.create("/social-login"))
                .body(socialUsecase.socialLogin(request));
    }

//    @GetMapping("/auth/authorize")
//    public ResponseEntity<String> socialLogin(@RequestBody @Valid SocialLoginRequest request){
//        String loginUrl = socialUsecase.getAuthorizeLogin(request);
//        return ResponseEntity.ok()
//    }

}
