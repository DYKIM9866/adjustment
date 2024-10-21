package com.sparta.adjustment.api.user.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@ToString
@Getter
public class SocialUserResponse {
    private String id;
    private String email;
    private String name;
    private String gender;
    private String birthday;
}
