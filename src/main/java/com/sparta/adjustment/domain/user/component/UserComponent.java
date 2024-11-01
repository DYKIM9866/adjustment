package com.sparta.adjustment.domain.user.component;

import com.sparta.adjustment.domain.user.User;
import com.sparta.adjustment.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserComponent {

    private final UserRepository userRepository;

    public User getUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("해당 유저가 존재 하지 않습니다."));

        return user;
    }

    public User getUser(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("해당 유저가 존재 하지 않습니다."));

        return user;
    }
}
