package com.sparta.adjustment.domain.history.repository;

import com.sparta.adjustment.domain.history.UserVideoHistory;
import com.sparta.adjustment.domain.history.UserVideoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserVideoHistoryRepository extends JpaRepository<UserVideoHistory, UserVideoId> {
    Optional<UserVideoHistory> findUserVideoHistoriesById(UserVideoId id);
}

