package com.sparta.adjustment.domain.history.component;

import com.sparta.adjustment.domain.history.UserVideoHistory;
import com.sparta.adjustment.domain.user.repository.UserVideoHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserHistoryComponent {

    private final UserVideoHistoryRepository userVideoHistoryRepository;

    public Optional<UserVideoHistory> getUserVideoHistory(Long userId, Long videoId) {
        return userVideoHistoryRepository.findTopByUserIdAndVideoIdOrOrderByCreatedAtDesc(userId, videoId);
    }

    public UserVideoHistory saveUserVideoHistory(UserVideoHistory userVideoHistory) {
        return userVideoHistoryRepository.save(userVideoHistory);
    }
}
