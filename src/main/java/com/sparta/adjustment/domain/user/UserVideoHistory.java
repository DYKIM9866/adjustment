package com.sparta.adjustment.domain.user;

import com.sparta.adjustment.domain.BaseTime;
import com.sparta.adjustment.domain.user.enums.ViewingStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class UserVideoHistory extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long videoId;

    @Column(nullable = false)
    private Integer exitTiming;

    @Column
    private ViewingStatus viewingStatus;

    @Column
    private Boolean views;

    @Column
    private int adViews;


}
