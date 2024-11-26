package com.sparta.adjustment.domain.video.repository;

import com.sparta.adjustment.domain.video.DayVideoLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface DayVideoLogRepository extends JpaRepository<DayVideoLog, Long> {

    @Query(value = """
    SELECT video_id
    FROM day_video_log 
    WHERE created_at >= TO_DATE(:referenceDate, 'YYYY-MM-DD')
    AND  created_at < TO_DATE(:referenceDate, 'YYYY-MM-DD') + INTERVAL '1 day'
    ORDER BY video_id ASC
    """,
    nativeQuery = true)
    List<Long> getLogIds(String referenceDate);

    @Query("""
    SELECT d.videoId
    FROM DayVideoLog d
    WHERE d.createdAt >= :startTime
    AND  d.createdAt < :endTime
    ORDER BY d.videoId ASC
    """)
    List<Long> getLogIds(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

}
