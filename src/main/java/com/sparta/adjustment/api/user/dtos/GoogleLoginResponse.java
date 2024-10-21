package com.sparta.adjustment.api.user.dtos;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GoogleLoginResponse {
    private String id;
    private String email;
    private String picture;
}
