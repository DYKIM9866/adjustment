package com.sparta.adjustment.domain.history.component;

import com.sparta.adjustment.domain.history.UserVideoCheckHistory;
import com.sparta.adjustment.domain.history.UserVideoHistory;
import com.sparta.adjustment.domain.history.UserVideoId;
import com.sparta.adjustment.domain.history.repository.UserVideoCheckHistoryRepository;
import com.sparta.adjustment.domain.history.repository.UserVideoHistoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class HistoryComponent {

    private final UserVideoHistoryRepository videoHistoryRepository;
    private final UserVideoCheckHistoryRepository checkHistoryRepository;

    public Optional<UserVideoHistory> getUserVideoHistory(UserVideoId id) {
        return videoHistoryRepository.findUserVideoHistoriesById(id);
    }

    public UserVideoHistory saveUserVideoHistory(UserVideoHistory userVideoHistory) {
        return videoHistoryRepository.save(userVideoHistory);
    }

    public UserVideoCheckHistory getUserVideoCheckHistory(Long videoId, Long userId){
        return checkHistoryRepository.findTopByVideoIdAndUserIdOrderByCreatedAtDesc(videoId, userId)
                .orElseThrow(()->new EntityNotFoundException("조회되는 기록이 없어서 기록에 실패했습니다."));
    }

    public UserVideoCheckHistory saveCheckHistory(UserVideoCheckHistory userVideoCheckHistory) {
        return checkHistoryRepository.save(userVideoCheckHistory);
    }
}
