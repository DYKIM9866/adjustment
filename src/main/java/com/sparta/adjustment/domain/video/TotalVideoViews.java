package com.sparta.adjustment.domain.video;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@Entity
public class TotalVideoViews {
    @Id
    @OneToOne(mappedBy = "totalVideoViews")
    private Video video;
    @Column
    private Long totalViews;
    @Column
    private Long adTotalViews;
}
