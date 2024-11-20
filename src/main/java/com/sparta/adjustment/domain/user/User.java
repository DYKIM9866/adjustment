package com.sparta.adjustment.domain.user;

import com.sparta.adjustment.domain.BaseTime;
import com.sparta.adjustment.domain.user.enums.SocialType;
import com.sparta.adjustment.domain.user.enums.UserAuth;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class User extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true,nullable = false)
    private String email;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserAuth auth;

    @Column
    @Enumerated(EnumType.STRING)
    private SocialType socialType;

}
