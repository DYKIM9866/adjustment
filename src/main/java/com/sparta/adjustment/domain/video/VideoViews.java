package com.sparta.adjustment.domain.video;

import jakarta.persistence.*;

@Entity
public class VideoViews {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long videoId;

    @Column
    private Long views;

    @Column
    private Long adVideoViews;


}
