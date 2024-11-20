package com.sparta.adjustment.domain.user.component;

import com.sparta.adjustment.api.dto.response.SocialUserResponse;
import com.sparta.adjustment.domain.user.User;
import com.sparta.adjustment.domain.user.enums.SocialType;
import com.sparta.adjustment.domain.user.enums.UserAuth;
import com.sparta.adjustment.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserComponent {

    private final UserRepository userRepository;

    public User getUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("해당 유저가 존재 하지 않습니다."));

        return user;
    }

    public Optional<User> getUser(String userEmail) {
        return userRepository.findByEmail(userEmail);
    }

    public void joinUser(SocialUserResponse socialUserResponse, SocialType socialType) {
        userRepository.save(User.builder()
                .email(socialUserResponse.getEmail())
                .auth(UserAuth.NORMAL)
                .socialType(socialType)
                .build());
    }
}
