package com.sparta.adjustment.domain.user.feign.google;

import com.sparta.adjustment.api.user.dtos.GoogleRequestAccessToken;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "googleAuth", url = "https://oauth2.googleapis.com")
public interface GoogleAuthApi {
    @PostMapping(value = "/token", produces = "application/json")
    ResponseEntity<String> getAccessToken(@RequestBody GoogleRequestAccessToken accessToken);
}
