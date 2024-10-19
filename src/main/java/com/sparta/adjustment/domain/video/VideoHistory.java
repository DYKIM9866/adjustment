package com.sparta.adjustment.domain.video;

import com.sparta.adjustment.domain.user.User;
import com.sparta.adjustment.entity.BaseTime;
import jakarta.persistence.*;

@Entity
public class VideoHistory extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", referencedColumnName = "id", nullable = false)
    private User userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "videoId", referencedColumnName = "id", nullable = false)
    private User videoId;

    @Column(nullable = false)
    private String status;

    @Column
    private Integer exitTiming;
}
