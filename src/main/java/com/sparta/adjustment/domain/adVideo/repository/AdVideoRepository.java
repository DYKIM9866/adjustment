package com.sparta.adjustment.domain.adVideo.repository;

import com.sparta.adjustment.domain.adVideo.AdVideo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AdVideoRepository extends JpaRepository<AdVideo, Long> {
    @Query("select a from AdVideo a where a.adVideoLen = :length and a.deleteAt = false " +
            "order by function('RANDOM')")
    AdVideo findRandomOneByLength(@Param("length") int length);
}
