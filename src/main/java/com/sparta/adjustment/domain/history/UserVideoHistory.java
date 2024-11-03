package com.sparta.adjustment.domain.history;

import com.sparta.adjustment.domain.BaseTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class UserVideoHistory extends BaseTime {
    @Id
    private Long userId;

    @Id
    private Long videoId;

    @Column
    private Integer exitTiming;
}
