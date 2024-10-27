package com.sparta.adjustment.domain.user.repository;

import com.sparta.adjustment.domain.user.UserVideoHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserVideoHistoryRepository extends JpaRepository<UserVideoHistory, Long> {

}
