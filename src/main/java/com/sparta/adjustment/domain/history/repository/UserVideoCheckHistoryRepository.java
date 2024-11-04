package com.sparta.adjustment.domain.history.repository;

import com.sparta.adjustment.domain.history.UserVideoCheckHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserVideoCheckHistoryRepository extends JpaRepository<UserVideoCheckHistory, Long> {
    Optional<UserVideoCheckHistory> findTopByVideoIdAndUserIdOrderByCreatedAtDesc(Long videoId, Long userId);
}
