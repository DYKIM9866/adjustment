package com.sparta.adjustment.domain.video;

import com.sparta.adjustment.domain.BaseTime;
import com.sparta.adjustment.domain.adjustment.Adjustment;
import com.sparta.adjustment.domain.video.enums.VideoStatus;
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
    @Enumerated(EnumType.STRING)
    private VideoStatus status;

    @Column(nullable = false)
    private String url;

    @OneToOne(mappedBy = "video")
    private Adjustment adjustment;
}
