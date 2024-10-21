package com.sparta.adjustment.api.user;

import com.sparta.adjustment.api.user.dtos.LoginResponse;
import com.sparta.adjustment.api.user.dtos.SocialLoginRequest;
import com.sparta.adjustment.api.user.usecase.SocialLoginUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final SocialLoginUseCase socialUsecase;

    @PostMapping("/social-login")
    public ResponseEntity<LoginResponse> socialLogin(@RequestBody @Valid SocialLoginRequest request){
        return ResponseEntity.created(URI.create("/social-login"))
                .body(socialUsecase.socialLogin(request));
    }

}
