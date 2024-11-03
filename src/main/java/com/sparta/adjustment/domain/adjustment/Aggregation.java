package com.sparta.adjustment.domain.adjustment;

import com.sparta.adjustment.domain.BaseTime;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Aggregation extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long videoId;

    @Column(nullable = false)
    private Long videoAmount;

    @Column(nullable = false)
    private Long adAmount;

    @Column(nullable = false)
    private Long views;

    @Column(nullable = false)
    private Long adViews;

    @Column(nullable = false)
    private LocalDateTime referenceDate;
}
