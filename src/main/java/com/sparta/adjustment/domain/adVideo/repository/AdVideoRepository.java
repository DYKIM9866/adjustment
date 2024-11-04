package com.sparta.adjustment.domain.adVideo.repository;

import com.sparta.adjustment.domain.adVideo.AdVideo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AdVideoRepository extends JpaRepository<AdVideo, Long> {

    @Query("select a from AdVideo a where a.category = :category and a.deleteAt = false  " +
            "order by function('RANDOM')")
    AdVideo findRandomOneByCategory(String category);
}
