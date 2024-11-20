package com.sparta.adjustment.api.dto.response;

import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NaverLoginResponse {
    @Builder.Default
    private Response response = Response.builder().build();

    private String resultCode;
    private String message;

    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private String id;
        private String nickName;
        private String profile_image;
        private String age;
        private String gender;
        private String email;
        private String mobile;
        private String name;
        private String birthyear;
        private String birthday;
    }
}
