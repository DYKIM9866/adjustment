package com.sparta.adjustment.domain.adVideo;

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

    @Column
    private Boolean deleteAt;
}
