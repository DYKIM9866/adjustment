package com.sparta.adjustment.domain.history;

import com.sparta.adjustment.domain.BaseTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserVideoCheckHistory extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long videoId;

    @Column(nullable = false)
    private Long exitTiming;

    @Column(nullable = false)
    private Long adViews;

    @Column(nullable = false)
    private Long viewingTime;

    public UserVideoCheckHistory(Long userId, Long videoId, Long adViews, Long viewingTime) {
        this.userId = userId;
        this.videoId = videoId;
        this.adViews = adViews;
        this.viewingTime = viewingTime;
    }
}
