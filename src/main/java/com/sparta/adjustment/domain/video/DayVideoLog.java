package com.sparta.adjustment.domain.video;

import com.sparta.adjustment.domain.BaseTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class DayVideoLog extends BaseTime {

    public DayVideoLog(Long videoId) {
        this.videoId = videoId;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long videoId;
}
