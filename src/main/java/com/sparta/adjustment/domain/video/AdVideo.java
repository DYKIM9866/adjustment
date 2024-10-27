package com.sparta.adjustment.domain.video;

import jakarta.persistence.*;

@Entity
public class AdVideo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String type;

    @Column
    private Integer adVideoLen;
}
