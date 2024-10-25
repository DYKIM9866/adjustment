package com.sparta.adjustment.domain.video.repository;

import com.sparta.adjustment.domain.video.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
}
