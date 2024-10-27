package com.sparta.adjustment.domain.video;

import com.sparta.adjustment.domain.BaseTime;
import jakarta.persistence.*;

@Entity
public class VideoHistory extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long videoId;

    @Column(nullable = false)
    private Integer exitTiming;

    @Column
    private int adViews;
}
