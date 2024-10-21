package com.sparta.adjustment.domain.user;

import jakarta.persistence.*;

@Entity
public class UserVideoHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", referencedColumnName = "id", nullable = false)
    private User userId;

    @Column
    private Long videoId;

    @Column(nullable = false)
    private Integer exitTiming;

    @Column
    private int adViews;
}
