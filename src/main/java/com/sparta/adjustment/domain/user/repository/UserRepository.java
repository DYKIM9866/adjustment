package com.sparta.adjustment.domain.user.repository;

import com.sparta.adjustment.domain.user.User;
import com.sparta.adjustment.domain.user.UserVideoHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);

}
