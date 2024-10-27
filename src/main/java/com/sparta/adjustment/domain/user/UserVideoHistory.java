package com.sparta.adjustment.domain.user;

import jakarta.persistence.*;

@Entity
public class UserVideoHistory {
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
