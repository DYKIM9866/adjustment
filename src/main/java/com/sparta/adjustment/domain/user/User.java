package com.sparta.adjustment.domain.user;

import com.sparta.adjustment.domain.user.enums.SocialType;
import com.sparta.adjustment.domain.video.Video;
import com.sparta.adjustment.domain.video.VideoHistory;
import com.sparta.adjustment.entity.BaseTime;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "user")
public class User extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true,nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String auth;

    @Column
    @Enumerated(EnumType.STRING)
    private SocialType userType;

    @OneToMany(mappedBy = "publisher")
    private List<Video> myVideos;

    @OneToMany(mappedBy = "userId")
    private List<UserVideoHistory> myVideoHistory;
}
