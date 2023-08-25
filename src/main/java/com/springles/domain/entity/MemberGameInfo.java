package com.springles.domain.entity;

import com.springles.domain.constants.GameRole;
import com.springles.domain.constants.Level;
import com.springles.domain.constants.ProfileImg;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class MemberGameInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_game_info_id")
    private Long id;    // 게임정보 아이디

    @Column(nullable = false)
    private String nickname;    // 게임 닉네임

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProfileImg profileImg;  // 프로필 이미지

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Level level; // 유저 레벨

    @Column(nullable = false)
    private Long exp;   // 유저 경험치

    @Enumerated(EnumType.STRING)
    private GameRole inGameRole;  // 게임 내 직업

    // entity 맵핑 필요
    @Column(nullable = false)
    private Long memberId;   // memberId
}
