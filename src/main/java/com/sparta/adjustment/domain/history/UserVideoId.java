package com.sparta.adjustment.domain.history;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class UserVideoId implements Serializable {
    private Long userId;
    private Long videoId;
}
