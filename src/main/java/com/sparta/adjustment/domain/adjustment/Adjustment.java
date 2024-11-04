package com.sparta.adjustment.domain.adjustment;

import com.sparta.adjustment.domain.BaseTime;
import com.sparta.adjustment.domain.video.Video;
import jakarta.persistence.*;

@Entity
public class Adjustment extends BaseTime {
    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    @MapsId
    private Video video;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long totalAmount;

    @Column(nullable = false)
    private Long totalViews;

    @Column(nullable = false)
    private Long adViews;

    @Column(nullable = false)
    private Long totalPlayTime;

}
