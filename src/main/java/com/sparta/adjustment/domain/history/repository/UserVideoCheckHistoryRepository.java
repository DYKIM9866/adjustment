package com.sparta.adjustment.domain.history.repository;

import com.sparta.adjustment.domain.history.UserVideoCheckHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserVideoCheckHistoryRepository extends JpaRepository<UserVideoCheckHistory, Long> {
}
