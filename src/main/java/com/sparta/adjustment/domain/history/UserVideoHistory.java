package com.sparta.adjustment.domain.history;

import com.sparta.adjustment.domain.BaseTime;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserVideoHistory extends BaseTime {
    @EmbeddedId
    private UserVideoId id;

    @Column
    private int exitTiming;
}
