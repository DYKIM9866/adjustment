package com.sparta.adjustment.domain.adjustment.repository;

import com.sparta.adjustment.api.dto.response.AdjustmentCheckResponse;
import com.sparta.adjustment.domain.adjustment.Aggregation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AggregationRepository extends JpaRepository<Aggregation, Long> {

    @Query("""
        SELECT
            v.id AS videoId,
            v.title AS title,
            v.url AS url,
            COALESCE(SUM(a.adAmount), 0) AS totalAdAmount,
            COALESCE(SUM(a.viewsAmount), 0) AS totalViewsAmount
        FROM Video v
        JOIN Aggregation a ON v.id = a.videoId
        JOIN (SELECT 
                agg.videoId as v_id, 
                MAX(agg.createdAt) AS latest_date
              FROM Aggregation agg
              GROUP BY v_id
             ) AS latest ON a.videoId = latest.v_id
                AND a.createdAt = latest.latest_date
        WHERE v.publisher = :userId
        GROUP BY  v.id
    """)
    List<AdjustmentCheckResponse> getVideosAggregationRecently(Long userId);

    @Query("""
    SELECT
        v.id AS videoId,
        v.title AS title,
        v.url AS url,
        COALESCE(SUM(a.adAmount), 0) AS totalAdAmount,
        COALESCE(SUM(a.viewsAmount), 0) AS totalViewsAmount
    FROM Video v
    JOIN Aggregation a ON v.id = a.videoId
    WHERE v.publisher = :userId
    AND a.createdAt BETWEEN :startDate AND :endDate
    GROUP BY v.id
    """)
    List<AdjustmentCheckResponse> getVideosAggregationBetween(Long userId,
                                                              LocalDateTime startDate,
                                                              LocalDateTime endDate);
    @Query("""
    SELECT
        v.id AS videoId,
        v.title AS title,
        v.url AS url,
        COALESCE(SUM(a.views), 0) AS views,
        COALESCE(SUM(a.adAmount), 0) AS totalAdAmount,
        COALESCE(SUM(a.viewsAmount), 0) AS totalViewsAmount
    FROM Video v
    JOIN Aggregation a ON v.id = a.videoId
    JOIN(
            SELECT 
                agg.videoId AS v_id, 
                MAX(agg.createdAt) AS latest_date
            FROM Aggregation agg
            GROUP BY v_id
        ) AS latest ON a.videoId = latest.v_id
            AND a.createdAt = latest.latest_date
    WHERE v.publisher = :userId
    GROUP BY v.id
    ORDER BY views DESC , v.id ASC
    LIMIT 5
    """)
    List<AdjustmentCheckResponse> getViewsTopVideo(Long userId);

    @Query("""
    SELECT
        v.id AS videoId,
        v.title AS title,
        v.url AS url,
        COALESCE(SUM(a.viewingTime), 0) AS viewingTime,
        COALESCE(SUM(a.adAmount), 0) AS totalAdAmount,
        COALESCE(SUM(a.viewsAmount), 0) AS totalViewsAmount
    FROM Video v
    JOIN Aggregation a ON v.id = a.videoId
    JOIN(
            SELECT 
                agg.videoId v_id, 
                MAX(agg.createdAt) AS latest_date
            FROM Aggregation agg
            GROUP BY v_id
        ) AS latest ON a.videoId = latest.v_id
            AND a.createdAt = latest.latest_date
    WHERE v.publisher = :userId
    GROUP BY v.id
    ORDER BY viewingTime DESC , v.id ASC
    LIMIT 5
    """)
    List<AdjustmentCheckResponse> getViewingTimeTopVideo(Long userId);

    @Query("SELECT MIN(id)" +
            "FROM Aggregation " +
            "WHERE createdAt BETWEEN :start AND :end")
    Long getMinId(LocalDateTime start, LocalDateTime end);

    @Query("SELECT MAX(id)" +
            "FROM Aggregation " +
            "WHERE createdAt BETWEEN :start AND :end")
    Long getMaxId(LocalDateTime start, LocalDateTime end);
}
