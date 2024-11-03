package com.sparta.adjustment.domain.history;

import com.sparta.adjustment.domain.BaseTime;
import jakarta.persistence.*;

@Entity
public class UserVideoCheckHistory extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long videoId;

    @Column(nullable = false)
    private Integer exitTiming;

    @Column(nullable = false)
    private Integer adViews;

    @Column(nullable = false)
    private Integer viewingTime;
}
