package com.sparta.adjustment.api.user.dtos;

import com.sparta.adjustment.domain.user.enums.SocialType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class SocialLoginRequest {
    @NotNull
    private SocialType socialType;
    @NotNull
    private String code;
}
