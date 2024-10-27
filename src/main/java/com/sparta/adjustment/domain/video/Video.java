package com.sparta.adjustment.domain.video;

import com.sparta.adjustment.domain.BaseTime;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Video extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Long publisher;

    @Column(nullable = false)
    private Integer videoLen;

    @Column(length = 1000)
    private String videoExplain;

    @Column(nullable = false)
    private String status;



}
