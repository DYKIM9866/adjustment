package com.sparta.adjustment.domain.user.repository;

import com.sparta.adjustment.domain.user.User;
import com.sparta.adjustment.domain.user.UserVideoHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserVideoHistoryRepository extends JpaRepository<UserVideoHistory, Long> {
    Optional<UserVideoHistory> findTopByUserIdAndVideoIdOrOrderByCreatedAtDesc(Long userId, Long videoId);

}
