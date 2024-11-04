package com.sparta.adjustment.domain.adVideo;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class AdVideo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String category;

    @Column
    private String url;

    @Column
    private Integer adVideoLen;

    @Column
    private Boolean deleteAt;
}
