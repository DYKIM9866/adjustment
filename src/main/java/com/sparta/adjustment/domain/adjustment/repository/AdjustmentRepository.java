package com.sparta.adjustment.domain.adjustment.repository;

import com.sparta.adjustment.domain.adjustment.Adjustment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AdjustmentRepository extends JpaRepository<Adjustment, Long> {
    @Query("SELECT MIN(id) " +
            "FROM Adjustment ")
    long findMinId();

    @Query("SELECT MAX(id) " +
            "FROM Adjustment ")
    long findMaxId();
}

