package com.sparta.adjustment.domain.video;

import com.sparta.adjustment.domain.user.User;
import com.sparta.adjustment.entity.BaseTime;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Video extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="publisher", referencedColumnName = "id", nullable = false)
    private User publisher;

    @Column(nullable = false)
    private Integer videoLen;

    @Column
    private String explain;

    @Column(nullable = false)
    private String status;

    @OneToMany(mappedBy = "videoId")
    private List<VideoHistory> videoHistories;


}
